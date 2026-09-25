import axios from 'axios'

export function toUserErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    if (!error.response) {
      return '网络暂时不可用，请检查连接后重试'
    }
    if (error.response.status === 401 || error.response.status === 403) {
      return '当前身份无权执行此操作'
    }
    if (error.response.status >= 500) {
      return '服务暂时不可用，请稍后重试'
    }
  }

  return '请求未完成，请稍后重试'
}
