/** 平台管理员读取调度任务及清理日志，修改计划或手动触发一次固定清理任务。 */
import { getJson, postJson, putJson } from './http'

export interface RetentionRun {
  jobCode?: string
  startedAt: string
  finishedAt: string
  status: '成功' | '失败'
  deleted: Record<string, number>
  workspaceFiles: number
  objectFiles: number
  localLogFiles: number
  errors: string[]
}

export interface RetentionRunsResponse { items: RetentionRun[] }

export interface ScheduledJob {
  code: string
  name: string
  javaClass: string
  frequency: ScheduleFrequency
  dayOfWeek: number | null
  dayOfMonth: number | null
  time: string
  zone: string
}

export type ScheduleFrequency = 'DAILY' | 'WEEKLY' | 'MONTHLY'
export interface ScheduledJobsResponse { items: ScheduledJob[] }

export interface ScheduleRequest {
  frequency: ScheduleFrequency
  dayOfWeek: number | null
  dayOfMonth: number | null
  time: string
}

/** 只接受服务端固定任务列表及有效的基础字段。 */
export async function fetchScheduledJobs(): Promise<ScheduledJobsResponse> {
  const value = await getJson<unknown>('/v1/scheduled-jobs')
  if (!value || typeof value !== 'object' || !('items' in value) || !Array.isArray(value.items)
      || !value.items.every((item: unknown) => item && typeof item === 'object'
        && 'code' in item && typeof item.code === 'string'
        && 'name' in item && typeof item.name === 'string'
        && 'javaClass' in item && typeof item.javaClass === 'string'
        && 'frequency' in item && ['DAILY', 'WEEKLY', 'MONTHLY'].includes(String(item.frequency))
        && 'dayOfWeek' in item && (item.dayOfWeek === null || typeof item.dayOfWeek === 'number')
        && 'dayOfMonth' in item && (item.dayOfMonth === null || typeof item.dayOfMonth === 'number')
        && 'time' in item && typeof item.time === 'string'
        && 'zone' in item && typeof item.zone === 'string')) {
    throw new Error('调度任务响应格式无法识别')
  }
  return { items: value.items as ScheduledJob[] }
}

/** 只修改固定清理任务的频率和时间，不接受用户提供的接口路径。 */
export async function updateRetentionSchedule(request: ScheduleRequest): Promise<ScheduledJob> {
  return putJson<ScheduledJob, ScheduleRequest>('/v1/scheduled-jobs/retention-cleanup/schedule', request)
}

/** 异步提交一次固定清理任务；不携带时间草稿或任意任务标识。 */
export async function executeRetentionNow(): Promise<void> {
  await postJson<void, Record<string, never>>('/v1/scheduled-jobs/retention-cleanup/runs', {})
}

/** 读取最近清理记录，并拒绝不符合最小响应结构的数据。 */
export async function fetchRetentionRuns(): Promise<RetentionRunsResponse> {
  const value = await getJson<unknown>('/v1/retention-runs')
  if (!value || typeof value !== 'object' || !('items' in value) || !Array.isArray(value.items)) {
    throw new Error('清理日志响应格式无法识别')
  }
  const items = value.items
  if (!items.every((item: unknown) => item && typeof item === 'object'
      && 'startedAt' in item && typeof item.startedAt === 'string'
      && 'finishedAt' in item && typeof item.finishedAt === 'string'
      && 'status' in item && (item.status === '成功' || item.status === '失败')
      && 'deleted' in item && item.deleted !== null && typeof item.deleted === 'object'
      && 'workspaceFiles' in item && typeof item.workspaceFiles === 'number'
      && 'objectFiles' in item && typeof item.objectFiles === 'number'
      && 'localLogFiles' in item && typeof item.localLogFiles === 'number'
      && 'errors' in item && Array.isArray(item.errors))) {
    throw new Error('清理日志响应格式无法识别')
  }
  return { items: items as RetentionRun[] }
}
