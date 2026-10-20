/** 调用后端受控模型交互接口，只接收结构化业务建议和非敏感元数据。 */
import { BusinessGuidanceRequest, BusinessGuidanceResponse, BusinessHandlingDraft, ModelEvidenceAnalysisResponse, ModelPlanResponse, parseBusinessGuidance, parseModelEvidenceAnalysis, parseModelPlan } from '../api/generated'
import { postForm, postJson } from './http'

export type { BusinessGuidanceResponse, BusinessHandlingDraft, ModelEvidenceAnalysisResponse, ModelPlanResponse }

/** 模型调用最长等待时间，覆盖后端最多两次模型调用与网络缓冲。 */
const MODEL_TIMEOUT = 240_000

/** 请求模型根据当前任务和已确认范围生成调查方案。 */
export async function generateModelPlan(taskId: string): Promise<ModelPlanResponse> {
  return parseModelPlan(await postJson<unknown, Record<string, never>>(`/v1/tasks/${taskId}/model-interactions/plan`, {}, { timeout: MODEL_TIMEOUT }, { idempotencyKey: createKey('model-plan') }))
}

/** 请求模型分析当前任务的脱敏证据摘要。 */
export async function analyzeEvidenceWithModel(taskId: string): Promise<ModelEvidenceAnalysisResponse> {
  return parseModelEvidenceAnalysis(await postJson<unknown, Record<string, never>>(`/v1/tasks/${taskId}/model-interactions/evidence-analysis`, {}, { timeout: MODEL_TIMEOUT }, { idempotencyKey: createKey('model-evidence') }))
}

/** 请求模型根据已创建任务的业务说明生成可编辑初稿。 */
export async function generateBusinessGuidance(taskId: string, attachments: File[] = []): Promise<BusinessGuidanceResponse> {
  if (attachments.length > 0) {
    const formData = new FormData()
    attachments.forEach((attachment) => formData.append('files', attachment, attachment.name))
    return parseBusinessGuidance(await postForm<unknown>(`/v1/tasks/${taskId}/model-interactions/guidance/attachments`, formData, { timeout: MODEL_TIMEOUT }, { idempotencyKey: createKey('business-guidance-attachment') }))
  }
  return parseBusinessGuidance(await postJson<unknown, Record<string, never>>(`/v1/tasks/${taskId}/model-interactions/guidance`, {}, { timeout: MODEL_TIMEOUT }, { idempotencyKey: createKey('business-guidance') }))
}

/** 用管理员的自然语言反馈修订当前结构化业务处理草稿。 */
export async function reviseBusinessGuidance(taskId: string, message: string, currentDraft: BusinessHandlingDraft): Promise<BusinessGuidanceResponse> {
  return parseBusinessGuidance(await postJson<unknown, BusinessGuidanceRequest>(`/v1/tasks/${taskId}/model-interactions/guidance`, { message, currentDraft }, { timeout: MODEL_TIMEOUT }, { idempotencyKey: createKey('business-guidance-revise') }))
}

/** 生成模型交互请求的前端关联键。 */
function createKey(prefix: string): string {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') return `${prefix}-${crypto.randomUUID()}`
  return `${prefix}-${Date.now()}-${Math.random().toString(16).slice(2)}`
}
