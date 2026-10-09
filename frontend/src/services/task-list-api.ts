/** 当前管理员最近任务的只读查询。 */
import { getJson } from './http'
import { TaskDetail, TaskListItem, parseTaskDetail, parseTaskList } from '../api/generated'
export type { TaskDetail, TaskListItem }
export async function listRecentTasks(): Promise<TaskListItem[]> { return parseTaskList(await getJson<unknown>('/v1/tasks')) }
/** 获取任务的只读业务、范围和方案详情。 */
export async function getTaskDetail(taskId: string): Promise<TaskDetail> { return parseTaskDetail(await getJson<unknown>(`/v1/tasks/${taskId}`)) }
