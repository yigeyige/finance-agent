## Context

后端已有步骤提议、步骤审批和任务公开事件接口，前端已有严格类型、幂等 REST 和游标 SSE 基础。

## Goals / Non-Goals
**Goals:**
- 展示受限计划、审批绑定、批准/拒绝和公开事件。
- 防止重复提交、旧版本审批和跨任务事件进入页面。

**Non-Goals:**
- 不实现执行控制、模型原始响应或新的后端状态机。

## Decisions
- 复用 Axios API 服务，以 `rowVersion` 和幂等键提交审批。
- 使用现有 `openSse`，SSE URL 携带 `lastEventId` 和 local/test 身份参数。
- 页面只渲染 `PublicTaskEvent` 的公开字段，不渲染任意 payload。

## Risks / Trade-offs
- [审批所需来源绑定由后续来源任务提供] → 当前表单显式要求输入绑定 UUID，不伪造授权数据。
- [EventSource 无自定义头] → local/test 使用白名单 `testIdentity` 查询参数，生产仍依赖 Principal。

## Migration Plan
前端静态构建即可；用户启动环境后手动验证计划、审批、重复点击和 SSE 重连。
