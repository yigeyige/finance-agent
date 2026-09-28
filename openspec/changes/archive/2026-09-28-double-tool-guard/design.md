## Decisions

1. 复用现有 `StepExecutionGuard` 做业务审批复核，`ToolCatalogService` 做工具目录和环境复核。
2. `PermitSigner` 使用进程内随机密钥的 HMAC-SHA256 签名，不把 permit 暴露为 HTTP 或模型令牌；篡改、过期、跨任务均拒绝。
3. `ToolGateway` 先验证 permit，再调用 `ToolInvocationService.startIntent`，因此幂等意图仍是外部副作用前的最后一道门。

## Non-Goals

- 不执行具体外部源调用；只交付授权和调用意图管线。
- 不引入新的依赖。

