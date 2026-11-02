/** 当前管理员最近任务的只读查询。 */
import { getJson } from './http'
import { TaskDetail, TaskListPage, parseTaskDetail, parseTaskList } from '../api/generated'
export type { TaskDetail, TaskListPage }
export interface TaskListQuery { page: number; pageSize: number; status?: string; keyword?: string }
/** 在当前可信身份范围内分页筛选任务。 */
export async function listTasks(query: TaskListQuery): Promise<TaskListPage> {
  const parameters = new URLSearchParams({ page: String(query.page), pageSize: String(query.pageSize) })
  if (query.status) parameters.set('status', query.status)
  if (query.keyword?.trim()) parameters.set('keyword', query.keyword.trim())
  return parseTaskList(await getJson<unknown>(`/v1/tasks?${parameters.toString()}`))
}
/** 获取任务的只读业务、范围和方案详情。 */
export async function getTaskDetail(taskId: string): Promise<TaskDetail> { return parseTaskDetail(await getJson<unknown>(`/v1/tasks/${taskId}`)) }

/** 查询任务流程产物入口状态，只接收布尔值，不读取场景提示词正文。 */
export async function getTaskProcessArtifactAvailability(taskId: string): Promise<boolean> {
  const response = await getJson<unknown>(`/v1/tasks/${taskId}/process-artifact-availability`)
  if (typeof response !== 'object' || response === null || !('available' in response) || typeof response.available !== 'boolean') {
    throw new Error('流程产物入口状态格式无法识别')
  }
  return response.available
}
