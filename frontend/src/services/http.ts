import axios, { AxiosError, AxiosInstance, AxiosRequestConfig } from 'axios'

import { toUserErrorMessage } from '../utils/error-message'

export interface ApiRequestOptions {
  idempotencyKey?: string
  rowVersion?: number
  requestId?: string
}

export class ApiError extends Error {
  readonly status?: number
  readonly code?: string
  readonly traceId?: string
  readonly body: unknown

  constructor(message: string, status: number | undefined, body: unknown, code?: string, traceId?: string) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.body = body
    this.code = code
    this.traceId = traceId
  }
}

export const httpClient: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
})

httpClient.interceptors.response.use(
  (response) => response,
  (error: unknown) => Promise.reject(createApiError(error)),
)

/** 发起带统一请求标识的 GET 请求。 */
export async function getJson<T>(url: string, config?: AxiosRequestConfig, options?: ApiRequestOptions): Promise<T> {
  const requestConfig = withRequestHeaders(config, options)
  const response = await httpClient.get<T>(url, requestConfig)
  return response.data
}

/** 发起带幂等键和行版本条件的 POST 请求。 */
export async function postJson<TResponse, TRequest>(
  url: string,
  payload: TRequest,
  config?: AxiosRequestConfig,
  options?: ApiRequestOptions,
): Promise<TResponse> {
  const response = await httpClient.post<TResponse>(url, payload, withRequestHeaders(config, options))
  return response.data
}

/** 生成请求头并保留调用方已有配置。 */
function withRequestHeaders(config?: AxiosRequestConfig, options?: ApiRequestOptions): AxiosRequestConfig {
  const headers = { ...(config?.headers ?? {}) }
  headers['X-Request-Id'] = options?.requestId ?? createRequestId()
  if (options?.idempotencyKey) headers['Idempotency-Key'] = options.idempotencyKey
  if (options?.rowVersion !== undefined) headers['If-Match'] = String(options.rowVersion)
  return { ...config, headers }
}

/** 将 Axios 或网络异常收敛为可识别的中文 API 错误。 */
function createApiError(error: unknown): ApiError {
  if (!axios.isAxiosError(error)) return new ApiError(toUserErrorMessage(error), undefined, undefined)
  const axiosError = error as AxiosError<unknown>
  const status = axiosError.response?.status
  const body = axiosError.response?.data
  const record = isRecord(body) ? body : undefined
  const code = typeof record?.code === 'string' ? record.code : undefined
  const traceId = typeof record?.traceId === 'string' ? record.traceId : undefined
  return new ApiError(toUserErrorMessage(error), status, body, code, traceId)
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null && !Array.isArray(value)
}

function createRequestId(): string {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') return crypto.randomUUID()
  return `web-${Date.now()}-${Math.random().toString(16).slice(2)}`
}
