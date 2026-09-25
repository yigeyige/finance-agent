export interface SseHandlers<TMessage> {
  onOpen?: () => void
  onMessage?: (message: TMessage) => void
  onError?: (error: Error) => void
}

export interface SseConnection {
  close: () => void
}

export function openSse<TMessage>(url: string, handlers: SseHandlers<TMessage>): SseConnection {
  const eventSource = new EventSource(url)

  eventSource.onopen = () => handlers.onOpen?.()
  eventSource.onmessage = (event: MessageEvent<string>) => {
    try {
      handlers.onMessage?.(JSON.parse(event.data) as TMessage)
    } catch {
      handlers.onError?.(new Error('实时消息格式暂时无法识别'))
    }
  }
  eventSource.onerror = () => {
    handlers.onError?.(new Error('实时连接暂时不可用，请稍后重试'))
  }

  return {
    close: () => eventSource.close(),
  }
}
