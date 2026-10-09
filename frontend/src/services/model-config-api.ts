import { getJson, postJson, putJson } from './http'
import { ModelConnectionConfig, ModelConnectionConfigPayload, parseModelConnectionConfig, parseModelConnectionConfigs } from '../api/generated'

export type { ModelConnectionConfig, ModelConnectionConfigPayload }

/** 获取管理员可维护的大模型连接列表。 */
export async function listModelConfigurations(): Promise<ModelConnectionConfig[]> { return parseModelConnectionConfigs(await getJson<unknown>('/v1/model-configurations')) }
/** 新增大模型连接。 */
export async function createModelConfiguration(payload: ModelConnectionConfigPayload): Promise<ModelConnectionConfig> { return parseModelConnectionConfig(await postJson<unknown, ModelConnectionConfigPayload>('/v1/model-configurations', payload)) }
/** 更新大模型连接，空 API Key 表示保留现有密钥。 */
export async function updateModelConfiguration(id: string, payload: ModelConnectionConfigPayload): Promise<ModelConnectionConfig> { return parseModelConnectionConfig(await putJson<unknown, ModelConnectionConfigPayload>(`/v1/model-configurations/${id}`, payload)) }
/** 将此模型设为唯一启用连接。 */
export function enableModelConfiguration(id: string): Promise<void> { return postJson<void, Record<string, never>>(`/v1/model-configurations/${id}/enable`, {}) }
/** 停用当前模型连接，保留配置和加密密钥以便后续重新启用。 */
export function disableModelConfiguration(id: string): Promise<void> { return postJson<void, Record<string, never>>(`/v1/model-configurations/${id}/disable`, {}) }
