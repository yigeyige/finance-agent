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

/** 请求模型根据当前任务和已确认范围生成调查方案。 */
export function generateModelPlan(taskId: string): Promise<ModelPlanResponse> {
  return postJson<ModelPlanResponse, Record<string, never>>(`/v1/tasks/${taskId}/model-interactions/plan`, {}, undefined, { idempotencyKey: createKey('model-plan') })
}

/** 请求模型分析当前任务的脱敏证据摘要。 */
export function analyzeEvidenceWithModel(taskId: string): Promise<ModelEvidenceAnalysisResponse> {
  return postJson<ModelEvidenceAnalysisResponse, Record<string, never>>(`/v1/tasks/${taskId}/model-interactions/evidence-analysis`, {}, undefined, { idempotencyKey: createKey('model-evidence') })
}

/** 生成模型交互请求的前端关联键。 */
function createKey(prefix: string): string {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') return `${prefix}-${crypto.randomUUID()}`
  return `${prefix}-${Date.now()}-${Math.random().toString(16).slice(2)}`
}
