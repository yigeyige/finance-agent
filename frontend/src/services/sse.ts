import { parsePublicTaskEvent, PublicTaskEvent } from '../api/generated'

export interface SseHandlers<TMessage> {
  onOpen?: () => void
  onMessage?: (message: TMessage) => void
  onError?: (error: Error) => void
}

export interface SseConnection {
  close: () => void
  resume: (cursor: number) => void
  getCursor: () => number | undefined
}

/** 打开支持任务事件游标恢复的 SSE 连接。 */
export function openSse<TMessage = PublicTaskEvent>(url: string, handlers: SseHandlers<TMessage>, initialCursor?: number): SseConnection {
  let cursor = initialCursor
  let eventSource = createEventSource(url, cursor, handlers, (nextCursor) => { cursor = nextCursor })

  const resume = (nextCursor: number) => {
    cursor = nextCursor
    eventSource.close()
    eventSource = createEventSource(url, cursor, handlers, (receivedCursor) => { cursor = receivedCursor })
  }

  return {
    close: () => eventSource.close(),
    resume,
    getCursor: () => cursor,
  }
}

/** 创建连接并在命名事件到达时校验和推进游标。 */
function createEventSource<TMessage>(url: string, cursor: number | undefined, handlers: SseHandlers<TMessage>, onCursor: (cursor: number) => void): EventSource {
  const target = new URL(url, window.location.origin)
  if (typeof localStorage !== 'undefined') {
    const testIdentity = localStorage.getItem('finance-agent.test-identity')
    if (testIdentity) target.searchParams.set('testIdentity', testIdentity)
  }
  if (cursor !== undefined) target.searchParams.set('lastEventId', String(cursor))
  const eventSource = new EventSource(target.toString())
  eventSource.onopen = () => handlers.onOpen?.()
  const handleMessage = (event: MessageEvent<string>) => {
    try {
      const parsed: unknown = JSON.parse(event.data)
      const message = parsePublicTaskEvent(parsed) as unknown as TMessage
      const sequence = (message as unknown as PublicTaskEvent).sequence
      const eventCursor = Number(event.lastEventId)
      onCursor(Number.isFinite(eventCursor) && eventCursor > 0 ? eventCursor : sequence)
      handlers.onMessage?.(message)
    } catch (error: unknown) {
      handlers.onError?.(error instanceof Error ? error : new Error('实时消息格式暂时无法识别'))
    }
  }
  eventSource.onmessage = handleMessage
  ;['TASK_STATUS_CHANGED', 'APPROVAL_REQUIRED', 'TOOL_PROGRESS', 'PUBLIC_REASON', 'RESULT_REFERENCE', 'TASK_PUBLISHED'].forEach((eventType) => {
    eventSource.addEventListener(eventType, handleMessage as EventListener)
  })
  eventSource.onerror = () => handlers.onError?.(new Error('实时连接暂时不可用，请稍后重试'))
  return eventSource
}
