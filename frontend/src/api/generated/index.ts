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
export interface PacketReviewResponse { taskId: string; draftId: string; contentDigest: string; scopeRevisionId: string; runtimeBundleId: string; decision: 'APPROVE' | 'REJECT'; reviewerUserId: string; reviewedAt: string; comment: string | null }
export interface PacketPublicationResponse { taskId: string; published: boolean; taskStatus: string; packetRevisionId: string | null; revisionNo: number | null; contentDigest: string | null; recheckDecision: string | null; recheckDigest: string | null; checkedAt: string | null; diffs: Array<{ objectKey: string; changes: string[] }> }
export interface PublishedPacketHandoff { handoffId: string; packetRevisionId: string; ownerReference: string; externalProcessReference: string | null; downstreamStatus: string; handedOffBy: string; handedOffAt: string }
export interface PublishedPacketResponse { taskId: string; packetRevisionId: string; revisionNo: number; canonicalJson: string; htmlSnapshot: string; contentDigest: string; checkedAt: string; sourceVersions: string; coverageSummary: string; limitations: string; publishedBy: string; publishedAt: string; taskStatus: TaskStatus; closedAt: string | null; handoff: PublishedPacketHandoff | null }
export interface PacketHandoffResponse { taskId: string; packetRevisionId: string; handoffId: string; ownerReference: string; externalProcessReference: string | null; downstreamStatus: string; taskStatus: TaskStatus; handedOffAt: string }
export interface TaskClosureResponse { taskId: string; taskStatus: TaskStatus; closedAt: string; handoffId: string; remediationComplete: false }
export interface TaskDiagnosticStep { stepId: string; sequenceNo: number; goal: string; status: string }
export interface TaskDiagnosticLease { status: 'NONE' | 'ACTIVE' | 'EXPIRED'; heartbeatAt: string | null; expiresAt: string | null }
export interface TaskDiagnosticResponse { taskId: string; taskStatus: TaskStatus; currentStep: TaskDiagnosticStep | null; lease: TaskDiagnosticLease; approvalValid: boolean; pauseReason: string | null; lastErrorCode: string | null; manualReconciliationRequired: boolean; recoveryActions: string[] }

/** 业务场景 Skill 的契约枚举与读写模型。 */
export type BusinessHandlingType = 'INVESTIGATE' | 'ADJUST'
export interface BusinessSceneSkill { skillId: string; name: string; description: string; handlingType: BusinessHandlingType; premiseType: string; updatedAt: string }
export interface BusinessSceneSkillPayload { name: string; description: string; handlingType: BusinessHandlingType; premiseType: string }

/** EMS 结构快照形成的只读业务域目录，不包含实时业务数据。 */
export interface EmsBusinessDomainKnowledge {
  code: string
  name: string
  summary: string
  schemas: string[]
  tableCount: number
  keyTables: string[]
  knowledgeBoundary: string
  verificationHint: string
}

/** 大模型连接安全视图；API Key 仅允许通过写入载荷传入。 */
export interface ModelConnectionConfig { configId: string; name: string; provider: string; apiUrl: string; modelName: string; reasoningEffort: string | null; enabled: boolean; apiKeyConfigured: boolean; updatedAt: string }
export interface ModelConnectionConfigPayload { name: string; provider: string; apiUrl: string; modelName: string; reasoningEffort: string; apiKey?: string }

/** EMS MySQL 连接安全视图；密码仅允许通过写入载荷传入。 */
export interface EmsDatabaseConnectionConfig { configId: string; name: string; jdbcUrl: string; username: string; connectTimeoutSeconds: number; queryTimeoutSeconds: number; maxRows: number; enabled: boolean; passwordConfigured: boolean; updatedAt: string }
export interface EmsDatabaseConnectionConfigPayload { name: string; jdbcUrl: string; username: string; password?: string; connectTimeoutSeconds: number; queryTimeoutSeconds: number; maxRows: number }

/** 数据探索仅返回已启用数据源的结构元数据与受限查询结果。 */
export interface EmsDatabaseTableMetadata { schema: string; table: string; comment: string }
export interface EmsDatabaseColumnMetadata { name: string; type: string; comment: string }
export interface DataExplorationCatalog { dataSourceName: string; maxRows: number; tables: EmsDatabaseTableMetadata[] }
export interface DataExplorationQueryRequest { sql: string }
export interface DataExplorationQueryResult { columns: string[]; rows: Array<Array<string | null>>; rowCount: number; elapsedMillis: number }

/** 任务列表与只读详情的契约模型。 */
export interface TaskListItem { taskId: string; title: string; status: string; initiatorUserId: string; createdAt: string }
export interface TaskListPage { items: TaskListItem[]; total: number; page: number; pageSize: number }
export interface TaskPremise { premiseType: string; subjectKey: string; statement: string; sourceDescription: string }
export interface TaskScope { systemCode: string; objectSelector: string; effectiveTimeRange: string; revisionNo: number }
export interface TaskPlan { sequenceNo: number; goal: string; recommended: boolean; status: string }
export interface TaskDetail { taskId: string; title: string; description: string; status: string; initiatorUserId: string; createdAt: string; premises: TaskPremise[]; scope: TaskScope | null; plans: TaskPlan[]; conclusion: string; evidence: DraftEvidence[] }

/** 业务方案修订与受控模型协作的契约模型。 */
export interface StepProposalRevisionPayload { goal: string }
export interface ModelCallMetadata { mode: 'LIVE' | 'STUB'; modelName: string; inputDigest: string; outputDigest: string; tokens: number; latencyMillis: number; reasoningEffort?: string }
export interface ModelPlanResponse { recommendedGoal: string; alternativeGoals: string[]; reason: string; metadata: ModelCallMetadata }
export interface ModelEvidenceAnalysisResponse { summary: string; riskLevel: 'LOW' | 'MEDIUM' | 'HIGH'; riskExplanation: string; recommendations: string[]; metadata: ModelCallMetadata }
export interface DraftEvidence { schema: string; table: string; purpose: string; columns: string[]; rows: string[][] }
export interface BusinessHandlingDraft { handlingType: BusinessHandlingType; premiseType: string; subjectKey: string; statement: string; sourceDescription: string; systemCode: string; objectKey: string; startDate: string; endDate: string; investigationAreas: string[]; dataUse: string; recommendedGoal: string; alternativeGoals: string[]; reason: string; conclusion: string; evidence: DraftEvidence[] }
export interface BusinessGuidanceRequest { message?: string; currentDraft?: BusinessHandlingDraft }
export interface BusinessGuidanceResponse { reply: string; draft: BusinessHandlingDraft; metadata: ModelCallMetadata }
export interface TaskDraftRequest { title: string; originalQuestion: string; initiatorUserId: string; runtimeBundleId: string; businessSceneSkillId?: string }

const TASK_RESPONSE_FIELDS = ['taskId', 'title', 'status', 'rowVersion', 'currentScopeRevisionId', 'createdAt', 'premiseId', 'scopeRevisionNo', 'scopeDigest']
const PUBLIC_EVENT_FIELDS = ['taskId', 'userId', 'sequence', 'eventType', 'status', 'publicReason', 'resultReference', 'occurredAt']
const IDENTITY_FIELDS = ['userId', 'authSubject', 'displayName', 'roles', 'permissions', 'testIdentity']
const BUSINESS_SCENE_SKILL_FIELDS = ['skillId', 'name', 'description', 'handlingType', 'premiseType', 'updatedAt']
const EMS_BUSINESS_DOMAIN_KNOWLEDGE_FIELDS = ['code', 'name', 'summary', 'schemas', 'tableCount', 'keyTables', 'knowledgeBoundary', 'verificationHint']
const MODEL_CONFIGURATION_FIELDS = ['configId', 'name', 'provider', 'apiUrl', 'modelName', 'reasoningEffort', 'enabled', 'apiKeyConfigured', 'updatedAt']
const EMS_DATABASE_CONFIGURATION_FIELDS = ['configId', 'name', 'jdbcUrl', 'username', 'connectTimeoutSeconds', 'queryTimeoutSeconds', 'maxRows', 'enabled', 'passwordConfigured', 'updatedAt']
const EMS_DATABASE_TABLE_METADATA_FIELDS = ['schema', 'table', 'comment']
const EMS_DATABASE_COLUMN_METADATA_FIELDS = ['name', 'type', 'comment']
const DATA_EXPLORATION_CATALOG_FIELDS = ['dataSourceName', 'maxRows', 'tables']
const DATA_EXPLORATION_QUERY_RESULT_FIELDS = ['columns', 'rows', 'rowCount', 'elapsedMillis']
const TASK_LIST_ITEM_FIELDS = ['taskId', 'title', 'status', 'initiatorUserId', 'createdAt']
const TASK_LIST_PAGE_FIELDS = ['items', 'total', 'page', 'pageSize']
const TASK_DETAIL_FIELDS = ['taskId', 'title', 'description', 'status', 'initiatorUserId', 'createdAt', 'premises', 'scope', 'plans', 'conclusion', 'evidence']
const TASK_PREMISE_FIELDS = ['premiseType', 'subjectKey', 'statement', 'sourceDescription']
const TASK_SCOPE_FIELDS = ['systemCode', 'objectSelector', 'effectiveTimeRange', 'revisionNo']
const TASK_PLAN_FIELDS = ['sequenceNo', 'goal', 'recommended', 'status']
const STEP_VIEW_FIELDS = ['stepId', 'sequenceNo', 'goal', 'recommended', 'status']
const MODEL_METADATA_FIELDS = ['mode', 'modelName', 'inputDigest', 'outputDigest', 'tokens', 'latencyMillis', 'reasoningEffort']
const BUSINESS_DRAFT_FIELDS = ['handlingType', 'premiseType', 'subjectKey', 'statement', 'sourceDescription', 'systemCode', 'objectKey', 'startDate', 'endDate', 'investigationAreas', 'dataUse', 'recommendedGoal', 'alternativeGoals', 'reason', 'conclusion', 'evidence']

/** 校验业务场景 Skill 响应。 */
export function parseBusinessSceneSkill(value: unknown): BusinessSceneSkill {
  const record = assertObjectFields(value, BUSINESS_SCENE_SKILL_FIELDS, '业务场景 Skill 响应')
  return { skillId: requireString(record.skillId, 'Skill 标识'), name: requireString(record.name, 'Skill 名称'), description: requireString(record.description, 'Skill 说明'), handlingType: parseBusinessHandlingType(record.handlingType), premiseType: requireString(record.premiseType, '业务场景'), updatedAt: requireString(record.updatedAt, '更新时间') }
}

/** 校验业务场景 Skill 列表响应。 */
export function parseBusinessSceneSkills(value: unknown): BusinessSceneSkill[] {
  return requireArray(value, '业务场景 Skill 列表').map(parseBusinessSceneSkill)
}

/** 校验 EMS 业务域知识响应。 */
export function parseEmsBusinessDomainKnowledge(value: unknown): EmsBusinessDomainKnowledge {
  const record = assertObjectFields(value, EMS_BUSINESS_DOMAIN_KNOWLEDGE_FIELDS, 'EMS业务知识域响应')
  return {
    code: requireString(record.code, '业务域编码'),
    name: requireString(record.name, '业务域名称'),
    summary: requireString(record.summary, '业务域说明'),
    schemas: requireStringArray(record.schemas, '所属数据库'),
    tableCount: requirePositiveNumber(record.tableCount, '结构快照表数量'),
    keyTables: requireStringArray(record.keyTables, '关键表'),
    knowledgeBoundary: requireString(record.knowledgeBoundary, '知识可信边界'),
    verificationHint: requireString(record.verificationHint, '核验提示'),
  }
}

/** 校验 EMS 业务域知识列表响应。 */
export function parseEmsBusinessDomainKnowledgeList(value: unknown): EmsBusinessDomainKnowledge[] {
  return requireArray(value, 'EMS业务知识域列表').map(parseEmsBusinessDomainKnowledge)
}

/** 校验不含密钥的大模型连接响应。 */
export function parseModelConnectionConfig(value: unknown): ModelConnectionConfig {
  const record = assertObjectFields(value, MODEL_CONFIGURATION_FIELDS, '大模型连接响应')
  return { configId: requireString(record.configId, '模型连接标识'), name: requireString(record.name, '连接名称'), provider: requireString(record.provider, '模型供应商'), apiUrl: requireString(record.apiUrl, '模型地址'), modelName: requireString(record.modelName, '模型名称'), reasoningEffort: nullableString(record.reasoningEffort), enabled: requireBoolean(record.enabled, '启用状态'), apiKeyConfigured: requireBoolean(record.apiKeyConfigured, '密钥状态'), updatedAt: requireString(record.updatedAt, '更新时间') }
}

/** 校验大模型连接列表响应。 */
export function parseModelConnectionConfigs(value: unknown): ModelConnectionConfig[] {
  return requireArray(value, '大模型连接列表').map(parseModelConnectionConfig)
}

/** 校验不含密码的 EMS 数据库连接响应。 */
export function parseEmsDatabaseConnectionConfig(value: unknown): EmsDatabaseConnectionConfig {
  const record = assertObjectFields(value, EMS_DATABASE_CONFIGURATION_FIELDS, 'EMS 数据库连接响应')
  return { configId: requireString(record.configId, 'EMS 数据库连接标识'), name: requireString(record.name, '连接名称'), jdbcUrl: requireString(record.jdbcUrl, '数据库地址'), username: requireString(record.username, '数据库账号'), connectTimeoutSeconds: requirePositiveNumber(record.connectTimeoutSeconds, '连接超时'), queryTimeoutSeconds: requirePositiveNumber(record.queryTimeoutSeconds, '查询超时'), maxRows: requirePositiveNumber(record.maxRows, '最大返回行数'), enabled: requireBoolean(record.enabled, '启用状态'), passwordConfigured: requireBoolean(record.passwordConfigured, '密码状态'), updatedAt: requireString(record.updatedAt, '更新时间') }
}

/** 校验 EMS 数据库连接列表响应。 */
export function parseEmsDatabaseConnectionConfigs(value: unknown): EmsDatabaseConnectionConfig[] {
  return requireArray(value, 'EMS 数据库连接列表').map(parseEmsDatabaseConnectionConfig)
}

/** 校验可展示的 EMS 表结构元数据。 */
export function parseEmsDatabaseTableMetadata(value: unknown): EmsDatabaseTableMetadata {
  const record = assertObjectFields(value, EMS_DATABASE_TABLE_METADATA_FIELDS, 'EMS 表目录响应')
  return { schema: requireString(record.schema, '数据库名称'), table: requireString(record.table, '表名'), comment: requireDisplayString(record.comment, '表注释') }
}

/** 校验可展示的 EMS 字段结构元数据。 */
export function parseEmsDatabaseColumnMetadata(value: unknown): EmsDatabaseColumnMetadata {
  const record = assertObjectFields(value, EMS_DATABASE_COLUMN_METADATA_FIELDS, 'EMS 字段结构响应')
  return { name: requireString(record.name, '字段名'), type: requireString(record.type, '字段类型'), comment: requireDisplayString(record.comment, '字段注释') }
}

/** 校验数据探索目录响应。 */
export function parseDataExplorationCatalog(value: unknown): DataExplorationCatalog {
  const record = assertObjectFields(value, DATA_EXPLORATION_CATALOG_FIELDS, '数据探索目录响应')
  return { dataSourceName: requireString(record.dataSourceName, '数据源名称'), maxRows: requirePositiveNumber(record.maxRows, '最大返回行数'), tables: requireArray(record.tables, '表目录').map(parseEmsDatabaseTableMetadata) }
}

/** 校验受限数据探索查询结果。 */
export function parseDataExplorationQueryResult(value: unknown): DataExplorationQueryResult {
  const record = assertObjectFields(value, DATA_EXPLORATION_QUERY_RESULT_FIELDS, '数据探索查询结果')
  const rows = requireArray(record.rows, '查询结果行').map((row) => requireArray(row, '查询结果单行').map((cell) => cell === null ? null : requireDisplayString(cell, '查询结果单元格')))
  return { columns: requireStringArray(record.columns, '查询结果列'), rows, rowCount: requireNonNegativeNumber(record.rowCount, '返回行数'), elapsedMillis: requireNonNegativeNumber(record.elapsedMillis, '执行耗时') }
}

/** 校验任务列表项。 */
export function parseTaskListItem(value: unknown): TaskListItem {
  const record = assertObjectFields(value, TASK_LIST_ITEM_FIELDS, '任务列表项')
  return { taskId: requireString(record.taskId, '任务标识'), title: requireString(record.title, '任务标题'), status: requireString(record.status, '任务状态'), initiatorUserId: requireString(record.initiatorUserId, '发起人标识'), createdAt: requireString(record.createdAt, '创建时间') }
}

/** 校验任务列表响应。 */
export function parseTaskList(value: unknown): TaskListPage {
  const record = assertObjectFields(value, TASK_LIST_PAGE_FIELDS, '任务列表')
  return { items: requireArray(record.items, '任务列表').map(parseTaskListItem), total: requireNonNegativeNumber(record.total, '任务总数'), page: requirePositiveNumber(record.page, '页码'), pageSize: requirePositiveNumber(record.pageSize, '每页条数') }
}

/** 校验推荐方案步骤响应。 */
export function parseStepView(value: unknown): StepView {
  const record = assertObjectFields(value, STEP_VIEW_FIELDS, '推荐方案步骤')
  return { stepId: requireString(record.stepId, '步骤标识'), sequenceNo: requirePositiveNumber(record.sequenceNo, '步骤序号'), goal: requireString(record.goal, '方案内容'), recommended: requireBoolean(record.recommended, '推荐标识'), status: requireString(record.status, '步骤状态') }
}

/** 校验任务只读详情响应。 */
export function parseTaskDetail(value: unknown): TaskDetail {
  const record = assertObjectFields(value, TASK_DETAIL_FIELDS, '任务详情')
  const scope = record.scope === null ? null : parseTaskScope(record.scope)
  return { taskId: requireString(record.taskId, '任务标识'), title: requireString(record.title, '任务标题'), description: requireString(record.description, '业务说明'), status: requireString(record.status, '任务状态'), initiatorUserId: requireString(record.initiatorUserId, '发起人标识'), createdAt: requireString(record.createdAt, '创建时间'), premises: requireArray(record.premises, '任务前提').map(parseTaskPremise), scope, plans: requireArray(record.plans, '推荐方案').map(parseTaskPlan), conclusion: typeof record.conclusion === 'string' ? record.conclusion : '', evidence: requireArray(record.evidence, '关联证据').map(parseDraftEvidence) }
}

/** 校验业务处理向导模型响应。 */
export function parseBusinessGuidance(value: unknown): BusinessGuidanceResponse {
  const record = assertObjectFields(value, ['reply', 'draft', 'metadata'], '模型业务初稿')
  return { reply: requireString(record.reply, '模型回复'), draft: parseBusinessHandlingDraft(record.draft), metadata: parseModelCallMetadata(record.metadata) }
}

/** 校验模型方案建议响应。 */
export function parseModelPlan(value: unknown): ModelPlanResponse {
  const record = assertObjectFields(value, ['recommendedGoal', 'alternativeGoals', 'reason', 'metadata'], '模型方案建议')
  return { recommendedGoal: requireString(record.recommendedGoal, '推荐方案'), alternativeGoals: requireStringArray(record.alternativeGoals, '备选方案'), reason: requireString(record.reason, '推荐理由'), metadata: parseModelCallMetadata(record.metadata) }
}

/** 校验模型证据分析响应。 */
export function parseModelEvidenceAnalysis(value: unknown): ModelEvidenceAnalysisResponse {
  const record = assertObjectFields(value, ['summary', 'riskLevel', 'riskExplanation', 'recommendations', 'metadata'], '模型证据分析')
  const riskLevel = record.riskLevel
  if (riskLevel !== 'LOW' && riskLevel !== 'MEDIUM' && riskLevel !== 'HIGH') throw new Error('风险等级格式无法识别，请联系管理员同步接口契约')
  return { summary: requireString(record.summary, '分析摘要'), riskLevel, riskExplanation: requireString(record.riskExplanation, '风险说明'), recommendations: requireStringArray(record.recommendations, '处理建议'), metadata: parseModelCallMetadata(record.metadata) }
}

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

/** 要求可展示文本为字符串；数据库元数据注释允许为空。 */
function requireDisplayString(value: unknown, label: string): string {
  if (typeof value !== 'string') throw new Error(`${label}格式无法识别，请稍后重试`)
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

/** 要求布尔值字段。 */
function requireBoolean(value: unknown, label: string): boolean {
  if (typeof value !== 'boolean') throw new Error(`${label}格式无法识别，请稍后重试`)
  return value
}

/** 要求数组字段。 */
function requireArray(value: unknown, label: string): unknown[] {
  if (!Array.isArray(value)) throw new Error(`${label}格式无法识别，请稍后重试`)
  return value
}

/** 要求字符串数组字段。 */
function requireStringArray(value: unknown, label: string): string[] {
  return requireArray(value, label).map((item) => requireString(item, label))
}

/** 收窄业务处理类型。 */
function parseBusinessHandlingType(value: unknown): BusinessHandlingType {
  if (value === 'INVESTIGATE' || value === 'ADJUST') return value
  throw new Error('处理方式格式无法识别，请联系管理员同步接口契约')
}

/** 校验任务前提。 */
function parseTaskPremise(value: unknown): TaskPremise {
  const record = assertObjectFields(value, TASK_PREMISE_FIELDS, '任务前提')
  return { premiseType: requireString(record.premiseType, '业务场景'), subjectKey: requireString(record.subjectKey, '处理对象'), statement: requireString(record.statement, '已知情况'), sourceDescription: requireString(record.sourceDescription, '信息来源') }
}

/** 校验任务范围。 */
function parseTaskScope(value: unknown): TaskScope {
  const record = assertObjectFields(value, TASK_SCOPE_FIELDS, '处理范围')
  return { systemCode: requireString(record.systemCode, '处理系统'), objectSelector: requireString(record.objectSelector, '对象范围'), effectiveTimeRange: requireString(record.effectiveTimeRange, '时间范围'), revisionNo: requirePositiveNumber(record.revisionNo, '范围版本') }
}

/** 校验任务方案。 */
function parseTaskPlan(value: unknown): TaskPlan {
  const record = assertObjectFields(value, TASK_PLAN_FIELDS, '任务方案')
  return { sequenceNo: requirePositiveNumber(record.sequenceNo, '方案序号'), goal: requireString(record.goal, '方案内容'), recommended: requireBoolean(record.recommended, '推荐标识'), status: requireString(record.status, '方案状态') }
}

/** 校验模型调用元数据。 */
function parseModelCallMetadata(value: unknown): ModelCallMetadata {
  const record = assertObjectFields(value, MODEL_METADATA_FIELDS, '模型调用信息')
  const mode = record.mode
  if (mode !== 'LIVE' && mode !== 'STUB') throw new Error('模型调用方式格式无法识别，请联系管理员同步接口契约')
  return { mode, modelName: requireString(record.modelName, '模型名称'), inputDigest: requireString(record.inputDigest, '输入摘要'), outputDigest: requireString(record.outputDigest, '输出摘要'), tokens: requireNonNegativeNumber(record.tokens, 'Token 数'), latencyMillis: requireNonNegativeNumber(record.latencyMillis, '调用耗时'), reasoningEffort: nullableString(record.reasoningEffort) ?? undefined }
}

/** 校验模型业务处理草稿。 */
function parseBusinessHandlingDraft(value: unknown): BusinessHandlingDraft {
  const record = assertObjectFields(value, BUSINESS_DRAFT_FIELDS, '业务处理草稿')
  return { handlingType: parseBusinessHandlingType(record.handlingType), premiseType: requireString(record.premiseType, '业务场景'), subjectKey: requireString(record.subjectKey, '处理对象'), statement: requireString(record.statement, '已知情况'), sourceDescription: requireString(record.sourceDescription, '信息来源'), systemCode: requireString(record.systemCode, '处理系统'), objectKey: requireString(record.objectKey, '对象标识'), startDate: requireString(record.startDate, '开始日期'), endDate: requireString(record.endDate, '结束日期'), investigationAreas: requireStringArray(record.investigationAreas, '处理内容'), dataUse: requireString(record.dataUse, '数据使用方式'), recommendedGoal: requireString(record.recommendedGoal, '推荐方案'), alternativeGoals: requireStringArray(record.alternativeGoals, '备选方案'), reason: requireString(record.reason, '推荐理由'), conclusion: typeof record.conclusion === 'string' ? record.conclusion : '', evidence: requireArray(record.evidence, '关联证据').map(parseDraftEvidence) }
}

/** 校验单条关联证据。 */
function parseDraftEvidence(value: unknown): DraftEvidence {
  const record = assertObjectFields(value, ['schema', 'table', 'purpose', 'columns', 'rows'], '关联证据')
  return { schema: requireString(record.schema, '证据库名'), table: requireString(record.table, '证据表名'), purpose: requireString(record.purpose, '证据用途'), columns: requireArray(record.columns, '证据列').map((column) => requireDisplayString(column, '证据列')), rows: requireArray(record.rows, '证据行').map((row) => requireArray(row, '证据行').map((cell) => requireDisplayString(cell, '证据行'))) }
}
