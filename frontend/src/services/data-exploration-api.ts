import { DataExplorationCatalog, DataExplorationQueryRequest, DataExplorationQueryResult, EmsDatabaseColumnMetadata, parseDataExplorationCatalog, parseDataExplorationQueryResult, parseEmsDatabaseColumnMetadata } from '../api/generated'
import { getJson, postJson } from './http'

export type { DataExplorationCatalog, DataExplorationQueryRequest, DataExplorationQueryResult, EmsDatabaseColumnMetadata }

/** 读取当前启用 EMS 数据源的受控表目录。 */
export async function fetchDataExplorationCatalog(): Promise<DataExplorationCatalog> {
  return parseDataExplorationCatalog(await getJson<unknown>('/v1/data-exploration/catalog'))
}

/** 读取一张允许 EMS 表的字段结构，不返回样本数据。 */
export async function fetchDataExplorationColumns(schema: string, table: string): Promise<EmsDatabaseColumnMetadata[]> {
  const result = await getJson<unknown>(`/v1/data-exploration/tables/${encodeURIComponent(schema)}/${encodeURIComponent(table)}/columns`)
  if (!Array.isArray(result)) throw new Error('数据探索字段响应格式不正确')
  return result.map(parseEmsDatabaseColumnMetadata)
}

/** 执行由服务端 Guard 进一步限制的单条 SELECT。 */
export async function executeDataExplorationQuery(sql: string): Promise<DataExplorationQueryResult> {
  return parseDataExplorationQueryResult(await postJson<unknown, DataExplorationQueryRequest>('/v1/data-exploration/queries', { sql }))
}
