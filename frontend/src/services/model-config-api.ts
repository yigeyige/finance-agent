import { getJson, postJson, putJson } from './http'

/** 后台大模型连接的安全展示结构，永不包含 API Key。 */
export interface ModelConnectionConfig {
  configId: string
  name: string
  provider: 'DEEPSEEK'
  apiUrl: string
  modelName: string
  reasoningEffort: string | null
  enabled: boolean
  apiKeyConfigured: boolean
  updatedAt: string
}

export interface ModelConnectionConfigPayload {
  name: string
  provider: 'DEEPSEEK'
  apiUrl: string
  modelName: string
  reasoningEffort: string
  apiKey?: string
}

/** 获取管理员可维护的大模型连接列表。 */
export function listModelConfigurations(): Promise<ModelConnectionConfig[]> { return getJson('/v1/model-configurations') }
/** 新增大模型连接。 */
export function createModelConfiguration(payload: ModelConnectionConfigPayload): Promise<ModelConnectionConfig> { return postJson('/v1/model-configurations', payload) }
/** 更新大模型连接，空 API Key 表示保留现有密钥。 */
export function updateModelConfiguration(id: string, payload: ModelConnectionConfigPayload): Promise<ModelConnectionConfig> { return putJson(`/v1/model-configurations/${id}`, payload) }
/** 将此模型设为唯一启用连接。 */
export function enableModelConfiguration(id: string): Promise<void> { return postJson<void, Record<string, never>>(`/v1/model-configurations/${id}/enable`, {}) }
