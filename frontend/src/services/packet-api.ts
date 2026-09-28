import { PacketDraftResponse, PacketPublicationResponse, PacketReviewResponse } from '../api/generated'
import { postJson } from './http'

export interface PacketDraftPayload {
  scopeRevisionId: string
  runtimeBundleId: string
  problem: string
  premises: unknown
  scope: unknown
  sources: unknown
  facts: unknown
  computations: unknown
  professionalJudgments: unknown
  recommendations: unknown
  businessConfirmations: unknown
  validationPreparation: unknown
  handoff: unknown
}

/** 组装结构化决策包草稿并返回 JSON 权威内容及 HTML 快照。 */
export function assemblePacketDraft(taskId: string, payload: PacketDraftPayload): Promise<PacketDraftResponse> {
  return postJson<PacketDraftResponse, PacketDraftPayload>(`/v1/tasks/${taskId}/packet-drafts`, payload, undefined, { idempotencyKey: createIdempotencyKey() })
}

export interface PacketReviewPayload { contentDigest: string; scopeRevisionId: string; runtimeBundleId: string; decision: 'APPROVE' | 'REJECT'; comment: string }

/** 提交绑定摘要、范围和运行版本的草稿复核。 */
export function reviewPacketDraft(taskId: string, draftId: string, payload: PacketReviewPayload): Promise<PacketReviewResponse> {
  return postJson<PacketReviewResponse, PacketReviewPayload>(`/v1/tasks/${taskId}/packet-drafts/${draftId}/reviews`, payload, undefined, { idempotencyKey: createIdempotencyKey() })
}

export interface PacketPublicationPayload {
  draftId: string
  publicationId: string
  contentDigest: string
  scopeRevisionId: string
  runtimeBundleId: string
  canonicalJson: string
  htmlSnapshot: string
  sourceVersions: string
  coverageSummary: string
  baseline: unknown[]
  current: unknown[]
}

/** 触发发布前全量重查，并在无变化时提交不可变发布修订。 */
export function publishPacket(taskId: string, payload: PacketPublicationPayload): Promise<PacketPublicationResponse> {
  return postJson<PacketPublicationResponse, PacketPublicationPayload>(`/v1/tasks/${taskId}/packet-publications`, payload, undefined, { idempotencyKey: createIdempotencyKey() })
}

function createIdempotencyKey(): string {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') return crypto.randomUUID()
  return `packet-${Date.now()}-${Math.random().toString(16).slice(2)}`
}
