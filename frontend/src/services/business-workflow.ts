/** 将业务人员可理解的表单和证据转换为后端严格契约，技术字段不进入页面输入。 */
import { EvidenceOverview } from '../api/generated'
import { PacketDraftPayload } from './packet-api'
import { ConfirmScopePayload } from './task-api'

export const DEFAULT_RUNTIME_BUNDLE_ID = '00000000-0000-4000-8000-000000000010'

export interface BusinessScopeForm {
  systemCode: string
  objectKey: string
  dateRange: string[]
  investigationAreas: string[]
  dataUse: 'MINIMUM' | 'ANALYSIS'
}

export interface PacketBusinessSummary {
  currentCount: number
  futureCount: number
  undeterminedCount: number
  findingCount: number
  coverageLabel: string
  recommendations: string[]
}

export interface ScopeSnapshotPayload {
  objectKey: string
  fieldsDigest: string
  effectiveClass: string
  sourceVersion: string
  ruleSetVersion: string
  templateVersion: string
}

export interface PreparedPacket {
  payload: PacketDraftPayload
  baseline: ScopeSnapshotPayload[]
  sourceVersions: string
  coverageSummary: string
  summary: PacketBusinessSummary
}

const AREA_DEFINITIONS: Record<string, { path: string; fields: string[]; goal: string }> = {
  APPROVAL: { path: 'workflow.approvals', fields: ['workflow.id', 'workflow.status', 'workflow.owner'], goal: '核对审批流程、节点责任人与当前有效状态' },
  ROLE: { path: 'role.assignments', fields: ['role.id', 'role.status', 'role.assignee'], goal: '核对角色权限和人员分配是否仍然有效' },
  ACCOUNTING: { path: 'accounting.references', fields: ['accounting.code', 'accounting.status', 'accounting.owner'], goal: '核对会计配置及其业务引用关系' },
  CONFIG: { path: 'configuration.references', fields: ['configuration.key', 'configuration.status', 'configuration.owner'], goal: '核对系统配置、引用关系和生效状态' },
}

/** 根据业务范围生成后端严格的范围确认请求。 */
export function buildScopePayload(form: BusinessScopeForm): ConfirmScopePayload {
  const definitions = form.investigationAreas.map((area) => AREA_DEFINITIONS[area]).filter(Boolean)
  const allFields = Array.from(new Set(definitions.flatMap((item) => item.fields)))
  const retainedFields = allFields.filter((field) => field.endsWith('.id') || field.endsWith('.code') || field.endsWith('.key'))
  return {
    systemCode: form.systemCode,
    objectSelector: { businessObject: form.objectKey },
    effectiveTimeRange: { from: form.dateRange[0], to: form.dateRange[1] },
    includedPaths: definitions.map((item) => item.path),
    excludedPaths: [],
    allowedReadFields: allFields,
    allowedRetainFields: retainedFields,
    allowedModelFields: form.dataUse === 'ANALYSIS' ? allFields : retainedFields,
    schemaVersion: 'scope-v1',
  }
}

/** 根据业务范围生成一个推荐调查步骤和最多两个备选步骤。 */
export function buildSuggestedGoals(title: string, areas: string[]): string[] {
  const goals = areas.map((area) => AREA_DEFINITIONS[area]?.goal).filter((goal): goal is string => Boolean(goal))
  return [`围绕“${title}”汇总全部已授权证据并形成结论`, ...goals].slice(0, 3)
}

/** 使用浏览器标准能力计算与后端一致的 SHA-256 十六进制摘要。 */
export async function sha256Hex(value: string): Promise<string> {
  const bytes = new TextEncoder().encode(value)
  const digest = await crypto.subtle.digest('SHA-256', bytes)
  return Array.from(new Uint8Array(digest)).map((byte) => byte.toString(16).padStart(2, '0')).join('')
}

/** 生成任务内技术绑定使用的 UUID，不要求业务人员理解或填写。 */
export function createWorkflowUuid(): string {
  if (typeof crypto.randomUUID === 'function') return crypto.randomUUID()
  const bytes = crypto.getRandomValues(new Uint8Array(16))
  bytes[6] = (bytes[6] & 0x0f) | 0x40
  bytes[8] = (bytes[8] & 0x3f) | 0x80
  const hex = Array.from(bytes).map((byte) => byte.toString(16).padStart(2, '0')).join('')
  return `${hex.slice(0, 8)}-${hex.slice(8, 12)}-${hex.slice(12, 16)}-${hex.slice(16, 20)}-${hex.slice(20)}`
}

/** 从脱敏证据生成完整决策包和发布基线。 */
export function preparePacketFromEvidence(evidence: EvidenceOverview, problem: string, impactConfirmed: boolean): PreparedPacket {
  const scopeRevisionId = evidence.findings.find((finding) => finding.scopeRevisionId)?.scopeRevisionId
  if (!scopeRevisionId) throw new Error('尚未形成调查结论，请先完成证据采集')
  if (!evidence.snapshots.length) throw new Error('尚未形成事实快照，请先完成调查执行')
  if (!evidence.coverages.length) throw new Error('尚未形成覆盖度结果，请先完成范围核查')

  const facts = {
    current: evidence.snapshots.filter((item) => item.effectiveClass === 'CURRENT_EFFECTIVE').map(toFact),
    future: evidence.snapshots.filter((item) => item.effectiveClass === 'FUTURE_SCHEDULED').map(toFact),
    undetermined: evidence.snapshots.filter((item) => !['CURRENT_EFFECTIVE', 'FUTURE_SCHEDULED'].includes(item.effectiveClass)).map(toFact),
  }
  const witnesses = evidence.findings.filter((finding) => finding.witness).map((finding) => ({ findingId: finding.findingId, ...finding.witness }))
  const judgments = evidence.findings.filter((finding) => finding.confirmation).map((finding) => ({ findingId: finding.findingId, statement: finding.statement, ...finding.confirmation }))
  const recommendations = evidence.findings.map((finding) => ({ findingId: finding.findingId, action: finding.statement, status: finding.status }))
  const ruleSetVersion = evidence.findings.find((finding) => finding.witness)?.witness?.ruleSetVersion || 'rules.current'
  const templateVersion = evidence.coverages[0]?.queryTemplateVersion || evidence.snapshots[0].schemaVersion
  const sourceVersion = evidence.snapshots[0].sourceVersion
  const baseline = evidence.snapshots.map((snapshot) => ({ objectKey: snapshot.sourceObjectKey, fieldsDigest: snapshot.resultDigest, effectiveClass: snapshot.effectiveClass, sourceVersion: snapshot.sourceVersion, ruleSetVersion, templateVersion }))
  const coverageLabel = evidence.coverages.every((item) => item.completeness === 'COMPLETE') ? '范围已完整核查' : '仍有范围需要补充核查'
  const summary = { currentCount: facts.current.length, futureCount: facts.future.length, undeterminedCount: facts.undetermined.length, findingCount: evidence.findings.length, coverageLabel, recommendations: evidence.findings.map((finding) => finding.statement) }
  const sourceVersions = JSON.stringify({ sourceVersion, ruleSetVersion, templateVersion })
  const coverageSummary = JSON.stringify({ completeness: evidence.coverages.every((item) => item.completeness === 'COMPLETE') ? 'COMPLETE' : 'PARTIAL', observedTotal: evidence.coverages.reduce((total, item) => total + (item.observedTotal || 0), 0), limitations: evidence.coverages.flatMap((item) => textList(item.uncoveredPaths)) })
  return {
    payload: {
      scopeRevisionId,
      runtimeBundleId: DEFAULT_RUNTIME_BUNDLE_ID,
      problem: problem.trim(),
      premises: { source: '当前任务已确认的业务前提' },
      scope: { taskId: evidence.taskId, scopeRevisionId },
      sources: Array.from(new Set(evidence.snapshots.map((item) => item.sourceVersion))).map((version) => ({ sourceVersion: version })),
      facts,
      computations: { witnesses },
      professionalJudgments: judgments,
      recommendations,
      businessConfirmations: { impactConfirmed, pendingItems: impactConfirmed ? [] : ['业务影响尚未确认'] },
      validationPreparation: { coverage: evidence.coverages, nextStep: coverageLabel },
      handoff: {},
    },
    baseline,
    sourceVersions,
    coverageSummary,
    summary,
  }
}

/** 从已发布的权威 JSON 提取业务摘要，解析失败时安全返回空摘要。 */
export function summarizeCanonicalPacket(canonicalJson: string): PacketBusinessSummary {
  try {
    const value = JSON.parse(canonicalJson) as Record<string, unknown>
    const facts = isRecord(value.facts) ? value.facts : {}
    const recommendations = Array.isArray(value.recommendations) ? value.recommendations : []
    return {
      currentCount: arrayLength(facts.current),
      futureCount: arrayLength(facts.future),
      undeterminedCount: arrayLength(facts.undetermined),
      findingCount: recommendations.length,
      coverageLabel: '已按发布时范围完成核查',
      recommendations: recommendations.map((item) => isRecord(item) && typeof item.action === 'string' ? item.action : '').filter(Boolean),
    }
  } catch {
    return { currentCount: 0, futureCount: 0, undeterminedCount: 0, findingCount: 0, coverageLabel: '业务摘要暂不可用', recommendations: [] }
  }
}

function toFact(snapshot: EvidenceOverview['snapshots'][number]): Record<string, unknown> {
  return { snapshotId: snapshot.snapshotId, objectType: snapshot.sourceObjectType, objectKey: snapshot.sourceObjectKey, checkedAt: snapshot.checkedAt, effectiveClass: snapshot.effectiveClass, sourceVersion: snapshot.sourceVersion, resultDigest: snapshot.resultDigest }
}

function textList(value: string): string[] {
  try { const parsed: unknown = JSON.parse(value); return Array.isArray(parsed) ? parsed.filter((item): item is string => typeof item === 'string') : value ? [value] : [] } catch { return value ? [value] : [] }
}

function isRecord(value: unknown): value is Record<string, unknown> { return typeof value === 'object' && value !== null && !Array.isArray(value) }
function arrayLength(value: unknown): number { return Array.isArray(value) ? value.length : 0 }
