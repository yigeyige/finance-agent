/** EMS 数据库实例管理接口：配置操作与管理员结构目录读取统一在此封装。 */
import { getJson, postJson, putJson } from './http'
import { EmsDatabaseConnectionConfig, EmsDatabaseConnectionConfigPayload, EmsDatabaseInstance, EmsSchemaCatalog, parseEmsDatabaseConnectionConfig, parseEmsDatabaseConnectionConfigs, parseEmsDatabaseInstances, parseEmsSchemaCatalog } from '../api/generated'

export type { EmsDatabaseConnectionConfig, EmsDatabaseConnectionConfigPayload, EmsDatabaseInstance, EmsSchemaCatalog }

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
/** 管理员手动更新实例的表字段结构目录，不读取业务行值。 */
export function syncEmsDatabaseSchema(id: string): Promise<unknown> { return postJson<unknown, Record<string, never>>(`/v1/ems-database-configurations/${id}/sync-schema`, {}) }
/** 按需读取指定实例已保存的结构目录；不会触发源库同步。 */
export async function getEmsDatabaseSchemaCatalog(id: string): Promise<EmsSchemaCatalog> { return parseEmsSchemaCatalog(await getJson<unknown>(`/v1/ems-database-configurations/${id}/schema-catalog`)) }
