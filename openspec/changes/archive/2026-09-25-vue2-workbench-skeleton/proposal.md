## Why

`frontend/` 目前只有占位文件，后续工作台任务缺少可复用的 Vue 2、路由、HTTP/SSE 和 Element UI 基础。现在建立最小可构建骨架，可以冻结前端技术基线，明确类型生成边界，并在不接入业务 API 或真实身份凭据的前提下让用户手工打开静态页面验证。

## What Changes

- 新增 Vue 2.7.16、TypeScript、Vite、Element UI 2.0.0 和 Vue Router 3 的前端工程。
- 新增基础布局、空路由、中文加载/错误状态与局部 Element UI 样式入口。
- 新增集中式 Axios HTTP 客户端、原生 EventSource SSE 客户端和生成类型边界目录。
- 锁定 Node/npm、直接依赖及传递依赖，记录 Vue 2/Element UI 停止维护风险和例外。
- 保持构建产物不包含 API Key、开发身份或其他运行时秘密。

## Capabilities

### New Capabilities

- `vue-workbench-foundation`: 定义 Vue 2 工作台工程、路由、布局、类型边界和构建契约。
- `frontend-transport-boundary`: 定义 HTTP/SSE 客户端集中管理和统一中文错误提示契约。

### Modified Capabilities

无。

## Impact

- 新增 `frontend/` 下的 package、锁文件、Vite/TypeScript 配置、源代码和静态资源。
- 新增前端版本说明文档，不修改后端接口或数据库。
- npm 安装和构建依赖网络，仅执行 `npm ci`、类型检查和生产构建，不启动开发服务器。
