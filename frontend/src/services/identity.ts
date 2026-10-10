import { CurrentIdentityResponse, IdentityPermission, parseCurrentIdentity } from '../api/generated'
import { getJson, postJson } from './http'

let currentIdentity: CurrentIdentityResponse | undefined
let identityPromise: Promise<CurrentIdentityResponse> | undefined

/** 查询后端当前身份和权限矩阵。 */
export async function fetchCurrentIdentity(): Promise<CurrentIdentityResponse> {
  const response = await getJson<unknown>('/v1/identity/me')
  currentIdentity = parseCurrentIdentity(response)
  return currentIdentity
}

/** 共享首次身份请求，供路由守卫和根组件等待同一结果。 */
export async function ensureCurrentIdentity(): Promise<CurrentIdentityResponse> {
  if (currentIdentity) return currentIdentity
  if (!identityPromise) identityPromise = fetchCurrentIdentity().finally(() => { identityPromise = undefined })
  return identityPromise
}

/** 返回最近一次身份查询结果，用于路由防御性判断。 */
export function getCurrentIdentity(): CurrentIdentityResponse | undefined {
  return currentIdentity
}

/** 判断当前身份是否拥有指定导航权限。 */
export function hasPermission(permission: IdentityPermission): boolean {
  // 平台管理员在工作台中具备全量导航权限；具体接口仍由后端权限守卫决定。
  return currentIdentity?.roles.includes('PLATFORM_ADMIN') || currentIdentity?.permissions.includes(permission) || false
}
/** 使用账号密码建立会话并更新当前身份缓存。 */
export async function login(username: string, password: string): Promise<CurrentIdentityResponse> {
  const response = await postJson<unknown, { username: string; password: string }>('/v1/identity/login', { username, password })
  currentIdentity = parseCurrentIdentity(response)
  return currentIdentity
}

/** 退出当前会话并清除前端身份缓存。 */
export async function logout(): Promise<void> {
  await postJson<void, Record<string, never>>('/v1/identity/logout', {})
  currentIdentity = undefined
  identityPromise = undefined
}
