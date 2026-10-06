import { CurrentIdentityResponse, IdentityPermission, parseCurrentIdentity } from '../api/generated'
import { getJson } from './http'

export const TEST_IDENTITIES = [
  { value: 'operator', label: '本地操作员' },
  { value: 'viewer', label: '本地查看员' },
  { value: 'platform-admin', label: '本地平台管理员' },
] as const

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
  return currentIdentity?.permissions.includes(permission) ?? false
}

/** 保存 local/test 身份选择并触发后续请求携带该标识。 */
export function selectTestIdentity(value: string): void {
  if (TEST_IDENTITIES.some((identity) => identity.value === value)) {
    localStorage.setItem('finance-agent.test-identity', value)
    currentIdentity = undefined
    identityPromise = undefined
  }
}
