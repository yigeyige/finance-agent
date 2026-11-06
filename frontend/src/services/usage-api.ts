/** 模型 token 用量接口：固定 30 天窗口，按当前身份隔离 5 分钟缓存。 */
import { TokenUsageSummary, parseTokenUsageSummary } from '../api/generated'
import { getJson } from './http'

const CACHE_TTL_MILLIS = 5 * 60 * 1000

let cachedUsage: { identityKey: string; expiresAt: number; summary: TokenUsageSummary } | null = null

/**
 * 读取当前身份最近 30 天的 token 用量。
 * 同一身份 5 分钟内复用缓存；切换账号时 identityKey 变化，不会读到上一个账号的数据。
 */
export async function fetchTokenUsage(identityKey: string): Promise<TokenUsageSummary> {
  if (cachedUsage && cachedUsage.identityKey === identityKey && cachedUsage.expiresAt > Date.now()) {
    return cachedUsage.summary
  }
  const summary = parseTokenUsageSummary(await getJson<unknown>('/v1/usage/tokens'))
  cachedUsage = { identityKey, expiresAt: Date.now() + CACHE_TTL_MILLIS, summary }
  return summary
}
