import { parseTaskResponse, TaskResponse } from '../api/generated'
import { ApiError, postJson } from './http'

export interface CreateTaskPayload { title: string; originalQuestion: string; initiatorUserId: string; runtimeBundleId: string; businessSceneSkillId?: string }
export interface AddPremisePayload { premiseType: string; subjectKey: string; statement: string; sourceDescription: string }
export interface ConfirmScopePayload { systemCode: string; objectSelector: unknown; effectiveTimeRange: unknown; includedPaths: unknown; excludedPaths: unknown; allowedReadFields: unknown; allowedRetainFields: unknown; allowedModelFields: unknown; schemaVersion: string }

/** 创建任务草稿并校验后端响应。 */
export async function createTask(payload: CreateTaskPayload): Promise<TaskResponse> {
  const response = await postJson<unknown, CreateTaskPayload>('/v1/tasks', withSceneSkill(payload), undefined, { idempotencyKey: createIdempotencyKey() })
  return parseTaskResponse(response)
}

/** 从业务场景列表跳转进入时，将哈希路由中的 Skill 标识持久化到任务创建请求。 */
function withSceneSkill(payload: CreateTaskPayload): CreateTaskPayload {
  if (payload.businessSceneSkillId || typeof window === 'undefined') return payload
  const query = window.location.hash.split('?')[1]
  const skillId = query ? new URLSearchParams(query).get('skillId') : null
  return skillId ? { ...payload, businessSceneSkillId: skillId } : payload
}

/** 补充任务前提来源并校验后端响应。 */
export async function addPremise(taskId: string, payload: AddPremisePayload, rowVersion: number): Promise<TaskResponse> {
  const response = await postJson<unknown, AddPremisePayload>(`/v1/tasks/${taskId}/premises`, payload, undefined, { idempotencyKey: createIdempotencyKey(), rowVersion })
  return parseTaskResponse(response)
}

/** 提交不可变范围修订并校验后端响应。 */
export async function confirmScope(taskId: string, payload: ConfirmScopePayload, rowVersion: number): Promise<TaskResponse> {
  const response = await postJson<unknown, ConfirmScopePayload>(`/v1/tasks/${taskId}/scope-confirmations`, payload, undefined, { idempotencyKey: createIdempotencyKey(), rowVersion })
  return parseTaskResponse(response)
}

/** 将表单 JSON 文本安全解析为请求字段。 */
export function parseJsonField(value: string, label: string): unknown {
  try { return JSON.parse(value) } catch { throw new Error(`${label}必须是合法 JSON`) }
}

/** 判断请求是否为并发版本冲突。 */
export function isStaleError(error: unknown): boolean { return error instanceof ApiError && (error.status === 409 || error.status === 412) }

function createIdempotencyKey(): string {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') return crypto.randomUUID()
  return `task-${Date.now()}-${Math.random().toString(16).slice(2)}`
}
