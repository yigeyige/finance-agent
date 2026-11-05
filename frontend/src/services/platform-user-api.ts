/** 平台用户管理接口与响应校验；密码只在提交时传输。 */
import { PlatformUser, RoleCode, parsePlatformUser } from '../api/generated'
import { deleteJson, getJson, postJson, putJson } from './http'
export type { PlatformUser }

export interface NewPlatformUser {
  authSubject: string
  employeeNo: string
  displayName: string
  role: RoleCode
  password: string
}

export interface EditPlatformUser { displayName: string; role: RoleCode }


/** 读取全部用户，包括停用账户。 */
export async function listPlatformUsers(): Promise<PlatformUser[]> {
  const response = await getJson<unknown>('/v1/platform-users')
  if (!Array.isArray(response)) throw new Error('用户列表响应格式无法识别')
  return response.map(parsePlatformUser)
}

/** 创建用户并设置初始密码。 */
export async function createPlatformUser(payload: NewPlatformUser): Promise<PlatformUser> {
  return parsePlatformUser(await postJson<unknown, NewPlatformUser>('/v1/platform-users', payload))
}

/** 修改用户显示名和角色。 */
export async function updatePlatformUser(userId: string, payload: EditPlatformUser): Promise<PlatformUser> {
  return parsePlatformUser(await putJson<unknown, EditPlatformUser>(`/v1/platform-users/${userId}`, payload))
}

/** 删除语义为停用，保留审计记录。 */
export function disablePlatformUser(userId: string): Promise<void> {
  return deleteJson(`/v1/platform-users/${userId}`)
}

/** 启用已停用用户。 */
export async function enablePlatformUser(userId: string): Promise<PlatformUser> {
  return parsePlatformUser(await postJson<unknown, Record<string, never>>(`/v1/platform-users/${userId}/enable`, {}))
}

/** 重置用户密码，不接收或展示旧密码。 */
export async function resetPlatformUserPassword(userId: string, password: string): Promise<void> {
  await postJson<void, { password: string }>(`/v1/platform-users/${userId}/password`, { password })
}
