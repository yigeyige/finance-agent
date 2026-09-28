import { EvidenceOverview } from '../api/generated'
import { getJson, postJson } from './http'

/** 查询当前任务的脱敏证据、覆盖度和判断视图。 */
export function fetchEvidenceOverview(taskId: string): Promise<EvidenceOverview> {
  return getJson<EvidenceOverview>(`/v1/tasks/${taskId}/evidence`)
}

export interface ProfessionalConfirmationPayload {
  scopeRevisionId: string
  decision: 'CONFIRM' | 'REJECT' | 'NEEDS_MORE_EVIDENCE'
  rationale: string
  evidenceRefs: string
}

export function confirmFinding(taskId: string, findingId: string, payload: ProfessionalConfirmationPayload): Promise<unknown> {
  return postJson<unknown, ProfessionalConfirmationPayload>(`/v1/tasks/${taskId}/findings/${findingId}/professional-confirmations`, payload, undefined, { idempotencyKey: createIdempotencyKey() })
}

function createIdempotencyKey(): string {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') return crypto.randomUUID()
  return `finding-${Date.now()}-${Math.random().toString(16).slice(2)}`
}
