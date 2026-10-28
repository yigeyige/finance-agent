import { DataExplorationCatalog, DataExplorationQueryRequest, DataExplorationQueryResult, EmsDatabaseColumnMetadata, parseDataExplorationCatalog, parseDataExplorationQueryResult, parseEmsDatabaseColumnMetadata } from '../api/generated'
import { getJson, postJson } from './http'

export type { DataExplorationCatalog, DataExplorationQueryRequest, DataExplorationQueryResult, EmsDatabaseColumnMetadata }

/** 读取指定 EMS 实例的受控表目录，未指定实例时由服务端默认第一个启用实例。 */
export async function fetchDataExplorationCatalog(configId?: string): Promise<DataExplorationCatalog> {
  const query = configId ? `?configId=${encodeURIComponent(configId)}` : ''
  return parseDataExplorationCatalog(await getJson<unknown>(`/v1/data-exploration/catalog${query}`))
}

/** 读取一张允许 EMS 表的字段结构，不返回样本数据。 */
export async function fetchDataExplorationColumns(schema: string, table: string, configId?: string): Promise<EmsDatabaseColumnMetadata[]> {
  const query = configId ? `?configId=${encodeURIComponent(configId)}` : ''
  const result = await getJson<unknown>(`/v1/data-exploration/tables/${encodeURIComponent(schema)}/${encodeURIComponent(table)}/columns${query}`)
  if (!Array.isArray(result)) throw new Error('数据探索字段响应格式不正确')
  return result.map(parseEmsDatabaseColumnMetadata)
}

/** 执行由服务端 Guard 进一步限制的单条 SELECT。 */
export async function executeDataExplorationQuery(sql: string, configId?: string): Promise<DataExplorationQueryResult> {
  return parseDataExplorationQueryResult(await postJson<unknown, DataExplorationQueryRequest>('/v1/data-exploration/queries', { sql, configId }))
}
