/** AI 预填接口：一次请求解析一段业务描述，不创建任务。 */
import { BusinessIntakeDraft, parseBusinessIntakeDraft } from '../api/generated'
import { postJson } from './http'

/** 后端预填超时为 60 秒，前端留出少量网络余量。 */
const INTAKE_TIMEOUT_MILLIS = 65_000

export async function requestBusinessIntakeDraft(text: string): Promise<BusinessIntakeDraft> {
  const response = await postJson<unknown, { text: string }>('/v1/business-intake/draft', { text },
    { timeout: INTAKE_TIMEOUT_MILLIS })
  return parseBusinessIntakeDraft(response)
}
