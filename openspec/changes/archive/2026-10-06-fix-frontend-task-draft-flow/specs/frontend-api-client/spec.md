## ADDED Requirements

### Requirement: Local development API proxy

前端 Vite 开发服务器 SHALL 将默认同源 `/api` 请求转发到本地后端，并 SHALL 允许通过开发环境变量覆盖后端目标地址；代理 MUST 保留完整 `/api` 路径，且生产构建不得固化本地后端地址。

#### Scenario: Default local API request reaches backend

- **WHEN** 开发者按标准命令启动监听 5173 的前端和监听 8080 的本地后端，且浏览器请求 `/api/v1/identity/me`
- **THEN** Vite 将请求转发到后端 `/api/v1/identity/me`，页面收到后端响应而不是前端开发服务器的 404

#### Scenario: Development target can be overridden

- **WHEN** 开发者设置合法的 `VITE_DEV_API_TARGET` 后启动 Vite
- **THEN** `/api` 请求转发到该目标，同时浏览器侧 API 基址仍保持同源 `/api`

