import {
  PacketHandoffResponse,
  PublishedPacketHandoff,
  PublishedPacketResponse,
  TaskClosureResponse,
  parseTaskStatus,
  assertObjectFields,
} from '../api/generated'
import { getJson, postJson } from './http'

export interface PacketHandoffPayload { ownerReference: string; externalProcessReference: string; downstreamStatus: string }
export interface TaskClosurePayload { closureReason: string }

/** 查询任务最新已发布决策包，OPERATOR/VIEWER 使用同一接口。 */
export async function fetchPublishedPacket(taskId: string): Promise<PublishedPacketResponse> {
  const response = await getJson<unknown>(`/v1/tasks/${taskId}/published-packet`)
  return parsePublishedPacketResponse(response)
}

/** 提交外部责任和流程交接。 */
export async function handoffPacket(taskId: string, revisionId: string, payload: PacketHandoffPayload): Promise<PacketHandoffResponse> {
  return postJson<PacketHandoffResponse, PacketHandoffPayload>(`/v1/tasks/${taskId}/packet-revisions/${revisionId}/handoff`, payload, undefined, { idempotencyKey: createIdempotencyKey() })
}

/** 关闭已交接调查，整改完成状态由后续流程确认。 */
export async function closeTask(taskId: string, payload: TaskClosurePayload): Promise<TaskClosureResponse> {
  return postJson<TaskClosureResponse, TaskClosurePayload>(`/v1/tasks/${taskId}/close`, payload, undefined, { idempotencyKey: createIdempotencyKey() })
}

function parsePublishedPacketResponse(value: unknown): PublishedPacketResponse {
  const record = assertObjectFields(value, ['taskId', 'packetRevisionId', 'revisionNo', 'canonicalJson', 'htmlSnapshot', 'contentDigest', 'checkedAt', 'sourceVersions', 'coverageSummary', 'limitations', 'publishedBy', 'publishedAt', 'taskStatus', 'closedAt', 'handoff'], '已发布决策包响应')
  return {
    taskId: requiredString(record.taskId, '任务标识'), packetRevisionId: requiredString(record.packetRevisionId, '修订标识'), revisionNo: requiredPositiveNumber(record.revisionNo, '修订版本'),
    canonicalJson: requiredString(record.canonicalJson, '权威 JSON'), htmlSnapshot: requiredString(record.htmlSnapshot, 'HTML 快照'), contentDigest: requiredString(record.contentDigest, '内容摘要'), checkedAt: requiredString(record.checkedAt, '核查时间'),
    sourceVersions: requiredString(record.sourceVersions, '来源版本'), coverageSummary: requiredString(record.coverageSummary, '覆盖摘要'), limitations: requiredString(record.limitations, '限制说明'), publishedBy: requiredString(record.publishedBy, '发布人'), publishedAt: requiredString(record.publishedAt, '发布时间'),
    taskStatus: parseTaskStatus(record.taskStatus), closedAt: nullableString(record.closedAt), handoff: parseHandoff(record.handoff),
  }
}

function parseHandoff(value: unknown): PublishedPacketHandoff | null {
  if (value === null || value === undefined) return null
  const record = assertObjectFields(value, ['handoffId', 'packetRevisionId', 'ownerReference', 'externalProcessReference', 'downstreamStatus', 'handedOffBy', 'handedOffAt'], '交接响应')
  return { handoffId: requiredString(record.handoffId, '交接标识'), packetRevisionId: requiredString(record.packetRevisionId, '交接修订标识'), ownerReference: requiredString(record.ownerReference, '责任引用'), externalProcessReference: nullableString(record.externalProcessReference), downstreamStatus: requiredString(record.downstreamStatus, '下游状态'), handedOffBy: requiredString(record.handedOffBy, '交接人'), handedOffAt: requiredString(record.handedOffAt, '交接时间') }
}

function requiredString(value: unknown, label: string): string { if (typeof value !== 'string' || value.length === 0) throw new Error(`${label}格式无法识别，请稍后重试`); return value }
function nullableString(value: unknown): string | null { return value === null || value === undefined ? null : requiredString(value, '响应字段') }
function requiredPositiveNumber(value: unknown, label: string): number { if (typeof value !== 'number' || !Number.isFinite(value) || value < 1) throw new Error(`${label}格式无法识别，请稍后重试`); return value }
function createIdempotencyKey(): string { return typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function' ? crypto.randomUUID() : `web-${Date.now()}-${Math.random().toString(16).slice(2)}` }
