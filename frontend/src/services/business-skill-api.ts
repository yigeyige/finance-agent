import { deleteJson, getJson, postJson, putJson } from './http'
import { BusinessSceneSkill, BusinessSceneSkillPayload, parseBusinessSceneSkill, parseBusinessSceneSkills } from '../api/generated'

export type { BusinessSceneSkill, BusinessSceneSkillPayload }

/** 查询全部可维护的业务场景 Skill。 */
export async function listBusinessSceneSkills(): Promise<BusinessSceneSkill[]> {
  return parseBusinessSceneSkills(await getJson<unknown>('/v1/business-scene-skills'))
}

/** 新增一个业务场景 Skill。 */
export async function createBusinessSceneSkill(payload: BusinessSceneSkillPayload): Promise<BusinessSceneSkill> {
  return parseBusinessSceneSkill(await postJson<unknown, BusinessSceneSkillPayload>('/v1/business-scene-skills', payload))
}

/** 更新一个业务场景 Skill。 */
export function updateBusinessSceneSkill(
  skillId: string,
  payload: BusinessSceneSkillPayload,
): Promise<BusinessSceneSkill> {
  return putJson<unknown, BusinessSceneSkillPayload>(`/v1/business-scene-skills/${skillId}`, payload).then(parseBusinessSceneSkill)
}

/** 删除一个业务场景 Skill。 */
export function deleteBusinessSceneSkill(skillId: string): Promise<void> {
  return deleteJson(`/v1/business-scene-skills/${skillId}`)
}
