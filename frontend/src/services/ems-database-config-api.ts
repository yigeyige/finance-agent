import { getJson, postJson, putJson } from './http'
import { EmsDatabaseConnectionConfig, EmsDatabaseConnectionConfigPayload, EmsDatabaseInstance, parseEmsDatabaseConnectionConfig, parseEmsDatabaseConnectionConfigs, parseEmsDatabaseInstances } from '../api/generated'

export type { EmsDatabaseConnectionConfig, EmsDatabaseConnectionConfigPayload, EmsDatabaseInstance }

/** 获取管理员可维护的 EMS 数据库连接。 */
export async function listEmsDatabaseConfigurations(): Promise<EmsDatabaseConnectionConfig[]> { return parseEmsDatabaseConnectionConfigs(await getJson<unknown>('/v1/ems-database-configurations')) }
/** 获取启用数据库实例列表，供业务处理创建时选择。 */
export async function listEnabledDatabaseInstances(): Promise<EmsDatabaseInstance[]> { return parseEmsDatabaseInstances(await getJson<unknown>('/v1/ems-database-configurations/enabled')) }
/** 新增 EMS 数据库连接。 */
export async function createEmsDatabaseConfiguration(payload: EmsDatabaseConnectionConfigPayload): Promise<EmsDatabaseConnectionConfig> { return parseEmsDatabaseConnectionConfig(await postJson<unknown, EmsDatabaseConnectionConfigPayload>('/v1/ems-database-configurations', payload)) }
/** 更新 EMS 数据库连接，空密码表示保留原密码。 */
export async function updateEmsDatabaseConfiguration(id: string, payload: EmsDatabaseConnectionConfigPayload): Promise<EmsDatabaseConnectionConfig> { return parseEmsDatabaseConnectionConfig(await putJson<unknown, EmsDatabaseConnectionConfigPayload>(`/v1/ems-database-configurations/${id}`, payload)) }
/** 启用该连接作为唯一的 EMS 只读数据源。 */
export function enableEmsDatabaseConfiguration(id: string): Promise<void> { return postJson<void, Record<string, never>>(`/v1/ems-database-configurations/${id}/enable`, {}) }
/** 停用该 EMS 数据源。 */
export function disableEmsDatabaseConfiguration(id: string): Promise<void> { return postJson<void, Record<string, never>>(`/v1/ems-database-configurations/${id}/disable`, {}) }
/** 仅执行固定 SELECT 1 探针验证连通性。 */
export function testEmsDatabaseConfiguration(id: string): Promise<void> { return postJson<void, Record<string, never>>(`/v1/ems-database-configurations/${id}/test`, {}) }
