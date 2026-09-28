import axios from 'axios'

/** 把传输层异常映射为稳定的中文用户提示。 */
export function toUserErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    if (!error.response) {
      return '网络暂时不可用，请检查连接后重试'
    }
    if (error.response.status === 401 || error.response.status === 403) {
      return '当前身份无权执行此操作'
    }
    if (error.response.status === 409) return '数据已发生冲突，请刷新后重试'
    if (error.response.status === 412) return '数据版本已过期，请刷新后重试'
    if (error.response.status >= 500) {
      return '服务暂时不可用，请稍后重试'
    }
  }

  return '请求未完成，请稍后重试'
}
