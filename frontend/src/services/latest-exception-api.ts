/** 最近异常堆栈只读接口：仅接收服务端已过滤的快照，不解析原始异常对象。 */
import { getJson } from './http'

export interface LatestExceptionSnapshot {
  occurredAt: string
  source: 'BUSINESS_API' | 'MODEL_INTERACTION'
  exceptionType: string
  stackTrace: string
}

export interface LatestExceptionResponse { item: LatestExceptionSnapshot | null }

/** 在页面进入时读取当前进程的最近一次异常，校验只读响应形状。 */
export async function fetchLatestException(): Promise<LatestExceptionResponse> {
  const response = await getJson<unknown>('/v1/latest-exception')
  if (!response || typeof response !== 'object' || !('item' in response)) {
    throw new Error('异常堆栈响应格式无法识别')
  }
  const item = response.item
  if (item === null) return { item: null }
  if (!item || typeof item !== 'object' || !('occurredAt' in item) || !('source' in item)
      || !('exceptionType' in item) || !('stackTrace' in item)
      || typeof item.occurredAt !== 'string'
      || (item.source !== 'BUSINESS_API' && item.source !== 'MODEL_INTERACTION')
      || typeof item.exceptionType !== 'string' || typeof item.stackTrace !== 'string') {
    throw new Error('异常堆栈响应格式无法识别')
  }
  return { item: { occurredAt: item.occurredAt, source: item.source,
    exceptionType: item.exceptionType, stackTrace: item.stackTrace } }
}
