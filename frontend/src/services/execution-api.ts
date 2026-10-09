import { StepApprovalResponse, StepProposalResponse } from '../api/generated'
import { postJson } from './http'

export interface ProposeStepsPayload { steps: { goal: string; recommended: boolean }[] }
export interface StepView { stepId: string; sequenceNo: number; goal: string; recommended: boolean; status: string }
export interface ApproveStepPayload { stepId: string; goalHash: string; sourceBindingId: string; scopeRevisionId: string; fieldPolicyVersion: string; queryTemplateVersion: string; maxCalls: number; expiresAt: string | null; expectedRowVersion: number; decision: 'APPROVED' | 'REJECTED' }

/** 提交一个推荐和最多两个备选步骤。 */
export async function proposeSteps(taskId: string, payload: ProposeStepsPayload, rowVersion: number): Promise<StepProposalResponse> {
  return postJson<StepProposalResponse, ProposeStepsPayload>(`/v1/tasks/${taskId}/step-proposals`, payload, undefined, { idempotencyKey: createKey(), rowVersion })
}

/** 保存尚未执行的推荐方案文本。 */
export async function reviseStepProposal(taskId: string, stepId: string, goal: string): Promise<StepView> {
  return postJson<StepView, { goal: string }>(`/v1/tasks/${taskId}/step-proposals/${stepId}`, { goal }, undefined, { idempotencyKey: createKey() })
}

/** 提交审批卡决定并返回新的任务版本。 */
export async function approveStep(taskId: string, payload: ApproveStepPayload): Promise<StepApprovalResponse> {
  return postJson<StepApprovalResponse, ApproveStepPayload>(`/v1/tasks/${taskId}/step-approvals`, payload, undefined, { idempotencyKey: createKey(), rowVersion: payload.expectedRowVersion })
}

function createKey(): string {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') return crypto.randomUUID()
  return `execution-${Date.now()}-${Math.random().toString(16).slice(2)}`
}
