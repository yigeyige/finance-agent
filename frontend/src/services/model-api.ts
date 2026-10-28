/** 调用后端受控模型交互接口，只接收结构化业务建议和非敏感元数据。 */
import { BusinessDecisionPacketResponse, BusinessEvidenceAccumResponse, BusinessGuidanceRequest, BusinessGuidanceResponse, BusinessHandlingDraft, ModelEvidenceAnalysisResponse, ModelPlanResponse, ModelRuntimeSummary, parseModelRuntimeSummary, parseBusinessDecisionPacket, parseBusinessEvidenceAccums, parseBusinessGuidance, parseModelEvidenceAnalysis, parseModelPlan } from '../api/generated'
import { getJson, postForm, postJson } from './http'

export type { BusinessDecisionPacketResponse, BusinessEvidenceAccumResponse, BusinessGuidanceResponse, BusinessHandlingDraft, ModelEvidenceAnalysisResponse, ModelPlanResponse }

/** 模型调用最长等待时间，覆盖后端选表、多轮只读查询与初稿生成的总耗时上限，避免链路被前端超时截断。 */
const MODEL_TIMEOUT = 600_000

/** 查询工作台可见的模型模式与名称，不读取管理员连接详情。 */
export async function getModelRuntimeSummary(): Promise<ModelRuntimeSummary> {
  return parseModelRuntimeSummary(await getJson<unknown>('/v1/model-runtime'))
}

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

/** 读取业务处理初稿生成的当前阶段进度，供等待期间轮询展示。 */
export async function getGuidanceProgress(taskId: string): Promise<{ stage: string }> {
  return getJson<{ stage: string }>(`/v1/tasks/${taskId}/model-interactions/guidance/progress`)
}

/** 生成业务决策包：读取证据工作区与最新草稿，去重合并后固化修订。 */
export async function generateBusinessDecisionPacket(taskId: string): Promise<BusinessDecisionPacketResponse> {
  return parseBusinessDecisionPacket(await postJson<unknown, Record<string, never>>(`/v1/tasks/${taskId}/decision-packet`, {}, { timeout: MODEL_TIMEOUT }, { idempotencyKey: createKey('business-decision-packet') }))
}

/** 读取任务证据工作区按轮累积的全部证据，供证据面板展示跨轮过程。 */
export async function getBusinessEvidenceAccums(taskId: string): Promise<BusinessEvidenceAccumResponse[]> {
  return parseBusinessEvidenceAccums(await getJson<unknown>(`/v1/tasks/${taskId}/decision-packet/evidence`))
}

/** 生成模型交互请求的前端关联键。 */
function createKey(prefix: string): string {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') return `${prefix}-${crypto.randomUUID()}`
  return `${prefix}-${Date.now()}-${Math.random().toString(16).slice(2)}`
}
