# Design: Permission/HITL 与工具网关桥

`HarnessPermissionMiddleware` 将 AgentScope `PermissionMode` 映射为 `WAITING_HITL` 或 `DENIED`。`HitlToolBridge` 在内存中以 requestId 保存短期待审批请求，返回不含参数原文和 permit 的 `HitlEvent`。

批准时 bridge 取出且检查 TTL，再把原始 `ToolGuardRequest` 交给现有 `ToolGateway.authorize`。因此最终 permit 仍由业务状态、审批、目录、范围和预算守卫签发；HITL 只是必要的前置门。拒绝、找不到或过期都返回结构化事件并删除待审批状态。
