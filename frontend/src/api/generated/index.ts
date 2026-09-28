/** 由版本化契约同步的前端领域类型与运行时校验边界。 */

export const TASK_STATUSES = ['DRAFT', 'WAITING_SCOPE_CONFIRMATION', 'READY', 'WAITING_STEP_APPROVAL', 'RUNNING_STEP', 'ANALYZING', 'WAITING_PROFESSIONAL_CONFIRMATION', 'DRAFT_PACKET', 'WAITING_REVIEW', 'RECHECKING_SCOPE', 'READY_TO_PUBLISH', 'PUBLISHED', 'HANDED_OFF', 'CLOSED', 'PAUSED_MODEL_UNAVAILABLE', 'PAUSED_BUDGET', 'PAUSED_SOURCE_UNAVAILABLE', 'STALE_APPROVAL', 'FAILED_RECOVERABLE', 'CANCELLED'] as const
export type TaskStatus = (typeof TASK_STATUSES)[number]

export const TASK_EVENT_TYPES = ['TASK_STATUS_CHANGED', 'APPROVAL_REQUIRED', 'TOOL_PROGRESS', 'PUBLIC_REASON', 'RESULT_REFERENCE', 'TASK_PUBLISHED'] as const
export type TaskEventType = (typeof TASK_EVENT_TYPES)[number]

export const ROLE_CODES = ['OPERATOR', 'VIEWER', 'PLATFORM_ADMIN'] as const
export type RoleCode = (typeof ROLE_CODES)[number]
export const IDENTITY_PERMISSIONS = ['TASK_READ', 'TASK_WRITE', 'TASK_EVENTS_READ', 'PUBLISHED_PACKET_READ', 'CONFIG_MANAGE'] as const
export type IdentityPermission = (typeof IDENTITY_PERMISSIONS)[number]

export interface TaskResponse {
  taskId: string
  title: string
  status: TaskStatus
  rowVersion: number
  currentScopeRevisionId: string | null
  createdAt: string
  premiseId: string | null
  scopeRevisionNo: number | null
  scopeDigest: string | null
}

export interface PublicTaskEvent {
  taskId: string
  userId: string
  sequence: number
  eventType: TaskEventType
  status: TaskStatus | null
  publicReason: string | null
  resultReference: string | null
  occurredAt: string
}

export interface ApiErrorPayload {
  code: string
  message: string
  retryClass: string
  traceId: string | null
}

export interface CurrentIdentityResponse {
  userId: string
  authSubject: string
  displayName: string
  roles: RoleCode[]
  permissions: IdentityPermission[]
  testIdentity: boolean
}

export interface StepProposalItem { goal: string; recommended: boolean }
export interface StepView { stepId: string; sequenceNo: number; goal: string; recommended: boolean; status: string }
export interface StepProposalResponse { taskId: string; status: TaskStatus; rowVersion: number; scopeRevisionId: string | null; currentStepId: string | null; steps: StepView[] }
export interface StepApprovalResponse { taskId: string; stepId: string | null; approvalId: string | null; revisionNo: number | null; status: string; approvalDigest: string | null; expiresAt: string | null; rowVersion: number; decision: string }
export interface EvidenceSnapshot { snapshotId: string; stepId: string; sourceObjectType: string; sourceObjectKey: string; checkedAt: string; effectiveFrom: string | null; effectiveTo: string | null; effectiveClass: string; sourceVersion: string; resultDigest: string; fieldPolicyVersion: string; schemaVersion: string }
export interface EvidenceCoverage { coverageId: string; stepId: string; queryTemplateVersion: string; pageCount: number | null; expectedTotal: number | null; observedTotal: number | null; traversedPaths: string; uncoveredPaths: string; failures: string; sourceWatermark: string; resultSetDigest: string; completeness: 'COMPLETE' | 'PARTIAL' | 'UNKNOWN'; checkedAt: string }
export interface EvidenceWitness { algorithmVersion: string; ruleSetVersion: string; inputDigest: string; outputDigest: string; snapshotIds: string; computedAt: string; recomputeStatus: string }
export interface EvidenceConfirmation { confirmationId: string; confirmedBy: string; confirmedAt: string; scopeRevisionId: string; decision: 'CONFIRM' | 'REJECT' | 'NEEDS_MORE_EVIDENCE'; rationale: string; evidenceRefs: string }
export interface EvidenceFinding { findingId: string; findingType: string; statement: string; judgmentMode: string; effectiveClass: string; status: string; scopeRevisionId: string; witness: EvidenceWitness | null; confirmation: EvidenceConfirmation | null }
export interface EvidenceOverview { taskId: string; snapshots: EvidenceSnapshot[]; coverages: EvidenceCoverage[]; findings: EvidenceFinding[] }
export interface PacketDraftResponse { taskId: string; draftId: string; draftNo: number; scopeRevisionId: string; runtimeBundleId: string; status: string; canonicalJson: string; contentDigest: string; htmlSnapshot: string; createdAt: string }

const TASK_RESPONSE_FIELDS = ['taskId', 'title', 'status', 'rowVersion', 'currentScopeRevisionId', 'createdAt', 'premiseId', 'scopeRevisionNo', 'scopeDigest']
const PUBLIC_EVENT_FIELDS = ['taskId', 'userId', 'sequence', 'eventType', 'status', 'publicReason', 'resultReference', 'occurredAt']
const IDENTITY_FIELDS = ['userId', 'authSubject', 'displayName', 'roles', 'permissions', 'testIdentity']

/** 校验对象并拒绝契约外字段，避免未声明状态流入页面。 */
export function assertObjectFields(value: unknown, fields: readonly string[], label: string): Record<string, unknown> {
  if (typeof value !== 'object' || value === null || Array.isArray(value)) {
    throw new Error(`${label}格式无法识别，请联系管理员同步接口契约`)
  }
  const record = value as Record<string, unknown>
  const extra = Object.keys(record).filter((key) => !fields.includes(key))
  if (extra.length > 0) {
    throw new Error(`${label}包含未声明字段，请联系管理员同步接口契约`)
  }
  return record
}

/** 将不可信状态收窄为已声明枚举。 */
export function parseTaskStatus(value: unknown): TaskStatus {
  if (typeof value === 'string' && (TASK_STATUSES as readonly string[]).includes(value)) {
    return value as TaskStatus
  }
  throw new Error('服务端返回了未知任务状态，请联系管理员同步接口契约')
}

/** 将不可信事件类型收窄为已声明枚举。 */
export function parseTaskEventType(value: unknown): TaskEventType {
  if (typeof value === 'string' && (TASK_EVENT_TYPES as readonly string[]).includes(value)) {
    return value as TaskEventType
  }
  throw new Error('实时消息类型暂不支持，请刷新页面后重试')
}

/** 校验任务响应并返回领域类型。 */
export function parseTaskResponse(value: unknown): TaskResponse {
  const record = assertObjectFields(value, TASK_RESPONSE_FIELDS, '任务响应')
  return {
    taskId: requireString(record.taskId, '任务标识'),
    title: requireString(record.title, '任务标题'),
    status: parseTaskStatus(record.status),
    rowVersion: requireNonNegativeNumber(record.rowVersion, '任务版本'),
    currentScopeRevisionId: nullableString(record.currentScopeRevisionId),
    createdAt: requireString(record.createdAt, '创建时间'),
    premiseId: nullableString(record.premiseId),
    scopeRevisionNo: nullableNonNegativeNumber(record.scopeRevisionNo, '范围版本'),
    scopeDigest: nullableString(record.scopeDigest),
  }
}

/** 校验公开任务事件并返回领域类型。 */
export function parsePublicTaskEvent(value: unknown): PublicTaskEvent {
  const record = assertObjectFields(value, PUBLIC_EVENT_FIELDS, '任务事件')
  return {
    taskId: requireString(record.taskId, '任务标识'),
    userId: requireString(record.userId, '用户标识'),
    sequence: requirePositiveNumber(record.sequence, '事件序列'),
    eventType: parseTaskEventType(record.eventType),
    status: record.status === null || record.status === undefined ? null : parseTaskStatus(record.status),
    publicReason: nullableString(record.publicReason),
    resultReference: nullableString(record.resultReference),
    occurredAt: requireString(record.occurredAt, '事件时间'),
  }
}

/** 校验当前身份响应并收窄角色和权限枚举。 */
export function parseCurrentIdentity(value: unknown): CurrentIdentityResponse {
  const record = assertObjectFields(value, IDENTITY_FIELDS, '身份响应')
  if (!Array.isArray(record.roles) || !record.roles.every((role) => typeof role === 'string' && (ROLE_CODES as readonly string[]).includes(role))) {
    throw new Error('身份角色格式无法识别，请联系管理员同步接口契约')
  }
  if (!Array.isArray(record.permissions) || !record.permissions.every((permission) => typeof permission === 'string' && (IDENTITY_PERMISSIONS as readonly string[]).includes(permission))) {
    throw new Error('身份权限格式无法识别，请联系管理员同步接口契约')
  }
  if (typeof record.testIdentity !== 'boolean') throw new Error('身份模式格式无法识别，请稍后重试')
  return {
    userId: requireString(record.userId, '用户标识'),
    authSubject: requireString(record.authSubject, '认证主体'),
    displayName: requireString(record.displayName, '显示名称'),
    roles: record.roles as RoleCode[],
    permissions: record.permissions as IdentityPermission[],
    testIdentity: record.testIdentity,
  }
}

/** 要求非空字符串字段。 */
function requireString(value: unknown, label: string): string {
  if (typeof value !== 'string' || value.length === 0) throw new Error(`${label}格式无法识别，请稍后重试`)
  return value
}

/** 要求非负数值字段。 */
function requireNonNegativeNumber(value: unknown, label: string): number {
  if (typeof value !== 'number' || !Number.isFinite(value) || value < 0) throw new Error(`${label}格式无法识别，请稍后重试`)
  return value
}

/** 要求从一开始计数的正数序列字段。 */
function requirePositiveNumber(value: unknown, label: string): number {
  if (typeof value !== 'number' || !Number.isFinite(value) || value < 1) throw new Error(`${label}格式无法识别，请稍后重试`)
  return value
}

/** 读取可空字符串字段。 */
function nullableString(value: unknown): string | null {
  if (value === null || value === undefined) return null
  return requireString(value, '响应字段')
}

/** 读取可空非负数值字段。 */
function nullableNonNegativeNumber(value: unknown, label: string): number | null {
  if (value === null || value === undefined) return null
  return requireNonNegativeNumber(value, label)
}
