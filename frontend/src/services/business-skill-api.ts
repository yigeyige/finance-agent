import { deleteJson, getJson, postJson, putJson } from './http'

export type BusinessHandlingType = 'INVESTIGATE' | 'ADJUST'

export interface BusinessSceneSkill {
  skillId: string
  name: string
  description: string
  handlingType: BusinessHandlingType
  premiseType: string
  updatedAt: string
}

export interface BusinessSceneSkillPayload {
  name: string
  description: string
  handlingType: BusinessHandlingType
  premiseType: string
}

/** 查询全部可维护的业务场景 Skill。 */
export function listBusinessSceneSkills(): Promise<BusinessSceneSkill[]> {
  return getJson<BusinessSceneSkill[]>('/v1/business-scene-skills')
}

/** 新增一个业务场景 Skill。 */
export function createBusinessSceneSkill(payload: BusinessSceneSkillPayload): Promise<BusinessSceneSkill> {
  return postJson<BusinessSceneSkill, BusinessSceneSkillPayload>('/v1/business-scene-skills', payload)
}

/** 更新一个业务场景 Skill。 */
export function updateBusinessSceneSkill(
  skillId: string,
  payload: BusinessSceneSkillPayload,
): Promise<BusinessSceneSkill> {
  return putJson<BusinessSceneSkill, BusinessSceneSkillPayload>(`/v1/business-scene-skills/${skillId}`, payload)
}

/** 删除一个业务场景 Skill。 */
export function deleteBusinessSceneSkill(skillId: string): Promise<void> {
  return deleteJson(`/v1/business-scene-skills/${skillId}`)
}
