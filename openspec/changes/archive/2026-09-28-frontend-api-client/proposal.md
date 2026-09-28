## Why

工作台目前只有零散的 Axios、EventSource 和错误提示骨架，领域响应、并发控制和断线恢复规则没有统一边界。现在建立契约类型与 API 客户端，可让前端在接入任务状态、幂等写入和公开事件流前获得可编译、可恢复且可审计的基础能力。

## What Changes

- 建立与 `contracts/` 版本化契约对应的 TypeScript 类型边界，并对未知枚举和额外字段执行运行时校验。
- 统一 REST 请求、请求标识、幂等键、行版本条件和中文错误模型。
- 扩展 SSE 客户端以携带游标、解析事件信封并支持断线恢复。
- 约束页面只消费领域类型和服务层结果，不自行拼接领域状态。

## Capabilities

### New Capabilities

- `frontend-api-contract`: 前端领域类型、严格响应校验与版本化契约边界。
- `frontend-api-client`: 统一 REST 客户端、并发控制与中文错误处理。
- `frontend-sse-client`: 可按游标恢复的公开任务 SSE 客户端。

### Modified Capabilities

## Impact

- 影响 `frontend/src/api/generated`、`frontend/src/services` 和 `frontend/src/utils`。
- 不新增 npm 依赖，继续使用现有 Axios、Vue 2.7.16 和 TypeScript。
- 后端接口路径和公开事件字段保持现有契约；浏览器人工检查请求头和游标行为。
