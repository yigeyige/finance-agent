import { deleteJson, getJson, postForm, postJson, putJson } from './http'
import { BusinessSceneSkill, BusinessSceneSkillPayload, SceneSkillAttachment, parseBusinessSceneSkill, parseBusinessSceneSkills, parseSceneSkillAttachment, parseSceneSkillAttachments } from '../api/generated'

export type { BusinessSceneSkill, BusinessSceneSkillPayload, SceneSkillAttachment }

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

/** 查询场景附件清单。 */
export async function listSceneSkillAttachments(skillId: string): Promise<SceneSkillAttachment[]> {
  return parseSceneSkillAttachments(await getJson<unknown>(`/v1/business-scene-skills/${skillId}/attachments`))
}

/** 上传场景附件（文件 + 摘要）。 */
export async function uploadSceneSkillAttachment(skillId: string, file: File, summary: string): Promise<SceneSkillAttachment> {
  const formData = new FormData()
  formData.append('file', file, file.name)
  formData.append('summary', summary)
  return parseSceneSkillAttachment(await postForm<unknown>(`/v1/business-scene-skills/${skillId}/attachments`, formData))
}

/** 删除场景附件。 */
export function deleteSceneSkillAttachment(skillId: string, attachmentId: string): Promise<void> {
  return deleteJson(`/v1/business-scene-skills/${skillId}/attachments/${attachmentId}`)
}
