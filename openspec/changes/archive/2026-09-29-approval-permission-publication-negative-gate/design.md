## Context

本任务是验证资产，不新增业务策略。现有服务已经把审批绑定、范围重查、复核绑定、发布事务和身份角色作为拒绝条件；新增资产通过固定合成 UUID、请求路径、角色、预期 HTTP 状态和审计事件名，帮助用户复现这些条件。

## Decisions

1. fixture 不含真实 token、凭据、业务数据或可用生产 ID；请求 body 只使用合成 UUID、摘要占位和最小字段。
2. 默认脚本仅做离线结构校验；`-Execute` 只扫描用户提供的响应/审计目录，不自动启动应用、不连接数据库、不发送请求。
3. 每个拒绝场景必须声明 `expectedHttpStatus`、`expectedDecision`、`auditEvent` 和 `request`，并要求人工证据至少包含响应、状态和审计记录。
4. 并发发布场景同时验证 publication idempotency key 的重放只返回同一 revision，不产生第二次发布；VIEWER 只能读取已发布包，不能审批、复核、发布、交接或关闭。

## Negative cases

- `SCOPE_NOT_CONFIRMED`
- `APPROVAL_EXPIRED`
- `FIELD_POLICY_OR_TEMPLATE_DRIFT`
- `CROSS_TASK_PERMIT`
- `PROFESSIONAL_CONFIRMATION_MISSING`
- `STALE_REVIEW`
- `CONCURRENT_PUBLICATION`
- `VIEWER_PUBLISH_FORBIDDEN`
- `VIEWER_REVIEW_FORBIDDEN`
- `VIEWER_HANDOFF_FORBIDDEN`
