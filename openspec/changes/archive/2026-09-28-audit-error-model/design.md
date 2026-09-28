## Decisions

1. `DomainErrorCode` 与 `RetryClass` 组成稳定错误目录；`ApiErrorResponse` 只输出 code、message、retryClass 和 traceId。
2. 全局异常处理器将参数、状态冲突、审计不可用和未知异常映射为固定安全消息，不回显异常原文。
3. `AuditedActionInterceptor` 先执行关键动作，失败时追加失败审计；审计追加失败则以 `AUDIT_WRITE_FAILED` fail closed。

## Non-Goals

- 不改变既有业务状态机和持久化协议。
- 不启动服务，不做人工失败响应验证。

