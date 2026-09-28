## Context

前端已有 Axios 与 EventSource 的最小封装，但错误被降级成普通 `Error`，请求头没有统一的幂等键和行版本，SSE 也未携带恢复游标。后端公开的任务响应包含 `taskId`、`status`、`rowVersion` 和事件序列，错误响应包含 `code`、`message`、`retryClass`、`traceId`。

## Goals / Non-Goals

**Goals:**

- 以生成边界目录集中维护可消费的领域类型和运行时校验。
- 为 REST 写请求提供请求标识、`Idempotency-Key` 和 `If-Match` 行版本条件。
- 为 SSE 提供游标参数、事件解析、未知事件保护和可重连连接。
- 保持页面只调用服务层，不复制状态字符串和错误映射。

**Non-Goals:**

- 不修改后端 API 路径、数据库或鉴权协议。
- 不引入新的代码生成器或运行时依赖。
- 不在本任务实现具体工作台页面交互。

## Decisions

1. **Axios 作为 REST 传输层。** 现有依赖已包含 Axios，保留其拦截器和取消能力；不改用手写 `fetch`，避免重复实现超时和响应适配。
2. **类型集中在 `src/api/generated/index.ts`。** 当前契约生成流水线尚未提供命令，因此先建立与版本化契约对齐的生成边界，并用严格类型检查和运行时白名单校验守住边界；后续生成器可替换该文件而不影响服务层。
3. **SSE 使用 EventSource + 游标查询参数。** 后端当前支持 `lastEventId`，浏览器原生 EventSource 会自动处理连接生命周期；客户端在首次连接和重连时携带最近事件序列，显式暴露 `resume`。
4. **错误使用可识别 `ApiError`。** 服务层保留 HTTP 状态、稳定错误码和 traceId，`toUserErrorMessage` 只负责中文用户提示，避免页面自行判断状态码。

## Risks / Trade-offs

- [契约生成命令尚未接入] → 将生成边界和版本说明写入 README，并在类型检查中阻止未声明字段进入页面。
- [EventSource 不支持自定义请求头] → 恢复游标放入后端已支持的 `lastEventId` 查询参数；鉴权继续沿用同源 Cookie。
- [服务端新增枚举] → 运行时校验将连接置为错误并给出中文提示，避免静默展示错误状态。

## Migration Plan

先替换共享服务和类型文件，再逐步让页面调用新服务层；旧导出函数保留兼容签名。验证通过后由用户启动环境，用浏览器检查请求头、409/412 提示和 SSE 游标恢复。

## Open Questions

- 后续接入正式 OpenAPI/JSON Schema 生成器时，生成文件的命名和脚本由前端基线任务另行确定。
