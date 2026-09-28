import { assertObjectFields, parseTaskStatus, TaskDiagnosticResponse, TaskDiagnosticStep } from '../api/generated'
import { getJson } from './http'

/** 查询任务脱敏诊断摘要。 */
export async function fetchTaskDiagnostics(taskId: string): Promise<TaskDiagnosticResponse> {
  const response = await getJson<unknown>(`/v1/tasks/${taskId}/diagnostics`)
  const record = assertObjectFields(response, ['taskId', 'taskStatus', 'currentStep', 'lease', 'approvalValid', 'pauseReason', 'lastErrorCode', 'manualReconciliationRequired', 'recoveryActions'], '任务诊断响应')
  return {
    taskId: requiredString(record.taskId, '任务标识'), taskStatus: parseTaskStatus(record.taskStatus), currentStep: parseStep(record.currentStep), lease: parseLease(record.lease),
    approvalValid: requiredBoolean(record.approvalValid, '审批状态'), pauseReason: nullableString(record.pauseReason), lastErrorCode: nullableString(record.lastErrorCode),
    manualReconciliationRequired: requiredBoolean(record.manualReconciliationRequired, '人工对账状态'), recoveryActions: parseActions(record.recoveryActions),
  }
}

function parseStep(value: unknown): TaskDiagnosticStep | null {
  if (value === null || value === undefined) return null
  const record = assertObjectFields(value, ['stepId', 'sequenceNo', 'goal', 'status'], '当前步骤')
  return { stepId: requiredString(record.stepId, '步骤标识'), sequenceNo: positiveNumber(record.sequenceNo, '步骤序号'), goal: requiredString(record.goal, '步骤目标'), status: requiredString(record.status, '步骤状态') }
}

function parseLease(value: unknown): TaskDiagnosticResponse['lease'] {
  const record = assertObjectFields(value, ['status', 'heartbeatAt', 'expiresAt'], '租约摘要')
  if (record.status !== 'NONE' && record.status !== 'ACTIVE' && record.status !== 'EXPIRED') throw new Error('租约状态格式无法识别，请稍后重试')
  return { status: record.status, heartbeatAt: nullableString(record.heartbeatAt), expiresAt: nullableString(record.expiresAt) }
}

function parseActions(value: unknown): string[] { if (!Array.isArray(value) || !value.every((item) => typeof item === 'string' && item.length > 0)) throw new Error('诊断动作格式无法识别，请稍后重试'); return value }
function requiredString(value: unknown, label: string): string { if (typeof value !== 'string' || value.length === 0) throw new Error(`${label}格式无法识别，请稍后重试`); return value }
function nullableString(value: unknown): string | null { return value === null || value === undefined ? null : requiredString(value, '响应字段') }
function requiredBoolean(value: unknown, label: string): boolean { if (typeof value !== 'boolean') throw new Error(`${label}格式无法识别，请稍后重试`); return value }
function positiveNumber(value: unknown, label: string): number { if (typeof value !== 'number' || !Number.isFinite(value) || value < 1) throw new Error(`${label}格式无法识别，请稍后重试`); return value }
