# Proposal: 集成 Permission/HITL 与工具网关

## Why

Harness permission 只表示执行层的许可，不能替代业务审批和 ToolGateway 二次守卫。当前缺少一个明确的暂停、人工决定和业务网关之间的桥，容易让 prompt 注入、伪造 permit 或动态工具绕过审批边界。

## What Changes

- 增加 Harness permission middleware，在工具请求处统一暂停到 HITL；BYPASS/DONT_ASK 也不会直接放行。
- 增加脱敏 HITL bridge，支持等待、批准、拒绝和过期事件。
- 人工批准后仍调用现有 `ToolGateway.authorize`，不在 Harness 层签发业务 permit；拒绝或过期不触达业务网关。

## Scope

不引入动态工具注册，不把 permit 暴露给模型或 HTTP，不修改现有业务审批和 ToolGateway 规则。
