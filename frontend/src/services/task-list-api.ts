/** 当前管理员最近任务的只读查询。 */
import { getJson } from './http'
export interface TaskListItem { taskId: string; title: string; status: string; createdAt: string }
export function listRecentTasks(): Promise<TaskListItem[]> { return getJson<TaskListItem[]>('/v1/tasks') }
export interface TaskDetail { taskId: string; title: string; description: string; status: string; createdAt: string; premises: TaskPremise[]; scope: TaskScope | null; plans: TaskPlan[] }
export interface TaskPremise { premiseType: string; subjectKey: string; statement: string; sourceDescription: string }
export interface TaskScope { systemCode: string; objectSelector: string; effectiveTimeRange: string; revisionNo: number }
export interface TaskPlan { sequenceNo: number; goal: string; recommended: boolean; status: string }
/** 获取任务的只读业务、范围和方案详情。 */
export function getTaskDetail(taskId: string): Promise<TaskDetail> { return getJson<TaskDetail>(`/v1/tasks/${taskId}`) }
