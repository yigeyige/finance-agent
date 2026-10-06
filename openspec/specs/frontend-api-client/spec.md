# frontend-api-client Specification

## Purpose
TBD - created by archiving change frontend-api-client. Update Purpose after archive.
## Requirements
### Requirement: Consistent REST request metadata
REST 客户端 SHALL 为请求生成请求标识，并支持写请求携带 `Idempotency-Key` 与 `If-Match` 行版本条件。

#### Scenario: Write request carries concurrency metadata
- **WHEN** 页面通过服务层提交幂等写请求
- **THEN** 请求包含请求标识、幂等键和行版本条件头

### Requirement: Stable Chinese error model
客户端 SHALL 将网络错误、401/403、409、412 和服务端错误映射为可识别的错误对象及明确中文提示。

#### Scenario: Version conflict
- **WHEN** 服务端返回 409 或 412
- **THEN** 客户端分别提示数据冲突或版本已过期，并保留 traceId

### Requirement: Local development API proxy

前端 Vite 开发服务器 SHALL 将默认同源 `/api` 请求转发到本地后端，并 SHALL 允许通过开发环境变量覆盖后端目标地址；代理 MUST 保留完整 `/api` 路径，且生产构建不得固化本地后端地址。

#### Scenario: Default local API request reaches backend

- **WHEN** 开发者按标准命令启动监听 5173 的前端和监听 8080 的本地后端，且浏览器请求 `/api/v1/identity/me`
- **THEN** Vite 将请求转发到后端 `/api/v1/identity/me`，页面收到后端响应而不是前端开发服务器的 404

#### Scenario: Development target can be overridden

- **WHEN** 开发者设置合法的 `VITE_DEV_API_TARGET` 后启动 Vite
- **THEN** `/api` 请求转发到该目标，同时浏览器侧 API 基址仍保持同源 `/api`
