import { EvidenceOverview } from '../api/generated'
import { getJson } from './http'

/** 查询当前任务的脱敏证据、覆盖度和判断视图。 */
export function fetchEvidenceOverview(taskId: string): Promise<EvidenceOverview> {
  return getJson<EvidenceOverview>(`/v1/tasks/${taskId}/evidence`)
}
