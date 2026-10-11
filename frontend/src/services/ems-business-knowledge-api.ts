import { EmsBusinessDomainKnowledge, parseEmsBusinessDomainKnowledgeList } from '../api/generated'
import { getJson } from './http'

export type { EmsBusinessDomainKnowledge }

/** 查询内置的 EMS 业务知识域目录，不读取 EMS 运行数据。 */
export async function listEmsBusinessDomainKnowledge(): Promise<EmsBusinessDomainKnowledge[]> {
  return parseEmsBusinessDomainKnowledgeList(await getJson<unknown>('/v1/ems-business-knowledge'))
}
