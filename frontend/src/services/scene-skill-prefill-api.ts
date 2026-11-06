/** 业务场景 AI 预填接口：一次生成一个目标字段，不修改场景。 */
import { SceneSkillPrefillResult, SceneSkillPrefillTarget, parseSceneSkillPrefillResult } from '../api/generated'
import { postJson } from './http'

/** 后端超时 60 秒，前端留出少量网络余量。 */
const PREFILL_TIMEOUT_MILLIS = 65_000

export interface SceneSkillPrefillPayload {
  target: SceneSkillPrefillTarget
  text: string
  skillId?: string
  databaseConfigId?: string
  sceneName?: string
  investigationPrompt?: string
  decisionPacketPrompt?: string
}

export async function requestSceneSkillPrefill(payload: SceneSkillPrefillPayload): Promise<SceneSkillPrefillResult> {
  const response = await postJson<unknown, SceneSkillPrefillPayload>('/v1/business-scene-skills/prefill', payload,
    { timeout: PREFILL_TIMEOUT_MILLIS })
  return parseSceneSkillPrefillResult(response)
}
