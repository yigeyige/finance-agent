import axios, { AxiosInstance, AxiosRequestConfig } from 'axios'

import { toUserErrorMessage } from '../utils/error-message'

export const httpClient: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
})

httpClient.interceptors.response.use(
  (response) => response,
  (error: unknown) => Promise.reject(new Error(toUserErrorMessage(error))),
)

export async function getJson<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  const response = await httpClient.get<T>(url, config)
  return response.data
}

export async function postJson<TResponse, TRequest>(
  url: string,
  payload: TRequest,
  config?: AxiosRequestConfig,
): Promise<TResponse> {
  const response = await httpClient.post<TResponse>(url, payload, config)
  return response.data
}
