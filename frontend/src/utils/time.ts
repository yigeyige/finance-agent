/** 工作台时间工具：API 使用 UTC，界面统一展示 Asia/Shanghai。 */
const SHANGHAI_TIME_ZONE = 'Asia/Shanghai'

/** 将 API UTC 时间格式化为工作台使用的上海时区文本。 */
export function formatUtcInShanghai(value: string | Date): string {
  const date = value instanceof Date ? value : new Date(value)
  if (Number.isNaN(date.getTime())) {
    return '时间不可用'
  }

  return new Intl.DateTimeFormat('zh-CN', {
    timeZone: SHANGHAI_TIME_ZONE,
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
  }).format(date)
}

/** 将浏览器 Date 转为 API 约定的 ISO-8601 UTC 字符串。 */
export function toApiUtc(value: Date): string {
  if (!(value instanceof Date) || Number.isNaN(value.getTime())) {
    throw new TypeError('时间值无效')
  }
  return value.toISOString()
}
