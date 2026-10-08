/** 调用后端受控模型交互接口，只接收结构化业务建议和非敏感元数据。 */
import { postJson } from './http'

export interface ModelCallMetadata {
  mode: 'LIVE' | 'STUB'
  modelName: string
  inputDigest: string
  outputDigest: string
  tokens: number
  latencyMillis: number
}

export interface ModelPlanResponse {
  recommendedGoal: string
  alternativeGoals: string[]
  reason: string
  metadata: ModelCallMetadata
}

export interface ModelEvidenceAnalysisResponse {
  summary: string
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH'
  riskExplanation: string
  recommendations: string[]
  metadata: ModelCallMetadata
}

/** 业务处理向导可编辑的对象、范围与方案草稿，不含技术绑定字段。 */
export interface BusinessHandlingDraft {
  handlingType: 'INVESTIGATE' | 'ADJUST'
  premiseType: string
  subjectKey: string
  statement: string
  sourceDescription: string
  systemCode: 'FIN-SYN'
  objectKey: string
  startDate: string
  endDate: string
  investigationAreas: Array<'APPROVAL' | 'ROLE' | 'ACCOUNTING' | 'CONFIG'>
  dataUse: 'MINIMUM' | 'ANALYSIS'
  recommendedGoal: string
  alternativeGoals: string[]
  reason: string
}

/** 业务处理模型初稿或会话修订的结构化响应。 */
export interface BusinessGuidanceResponse {
  reply: string
  draft: BusinessHandlingDraft
  metadata: ModelCallMetadata
}

/** 请求模型根据当前任务和已确认范围生成调查方案。 */
export function generateModelPlan(taskId: string): Promise<ModelPlanResponse> {
  return postJson<ModelPlanResponse, Record<string, never>>(`/v1/tasks/${taskId}/model-interactions/plan`, {}, undefined, { idempotencyKey: createKey('model-plan') })
}

/** 请求模型分析当前任务的脱敏证据摘要。 */
export function analyzeEvidenceWithModel(taskId: string): Promise<ModelEvidenceAnalysisResponse> {
  return postJson<ModelEvidenceAnalysisResponse, Record<string, never>>(`/v1/tasks/${taskId}/model-interactions/evidence-analysis`, {}, undefined, { idempotencyKey: createKey('model-evidence') })
}

/** 请求模型根据已创建任务的业务说明生成可编辑初稿。 */
export function generateBusinessGuidance(taskId: string): Promise<BusinessGuidanceResponse> {
  return postJson<BusinessGuidanceResponse, Record<string, never>>(`/v1/tasks/${taskId}/model-interactions/guidance`, {}, undefined, { idempotencyKey: createKey('business-guidance') })
}

/** 用管理员的自然语言反馈修订当前结构化业务处理草稿。 */
export function reviseBusinessGuidance(taskId: string, message: string, currentDraft: BusinessHandlingDraft): Promise<BusinessGuidanceResponse> {
  return postJson<BusinessGuidanceResponse, { message: string; currentDraft: BusinessHandlingDraft }>(`/v1/tasks/${taskId}/model-interactions/guidance`, { message, currentDraft }, undefined, { idempotencyKey: createKey('business-guidance-revise') })
}

/** 生成模型交互请求的前端关联键。 */
function createKey(prefix: string): string {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') return `${prefix}-${crypto.randomUUID()}`
  return `${prefix}-${Date.now()}-${Math.random().toString(16).slice(2)}`
}
