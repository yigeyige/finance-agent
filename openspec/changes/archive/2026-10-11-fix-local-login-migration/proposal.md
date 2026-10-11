## Why

本地启动脚本只拉起前后端，未执行数据库迁移，导致新增的登录凭据表和默认管理员数据没有落库，登录接口只能返回 401。启动入口应完成本地开发所需的迁移前置步骤，避免代码版本与数据库版本脱节。

## What Changes

- 本地启动脚本在启动后端前，使用已导入的 `.env` 进程变量调用项目 Flyway 迁移入口。
- 迁移失败时立即停止启动链路，不启动后端和前端；成功时继续原有端口复用及后台启动行为。
- 为本地启动流程补充迁移顺序和失败关闭的静态验证。
- 登录页成功跳转工作台时同步根组件身份状态，避免首次登录侧边菜单为空。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `local-development-startup`: 统一本地启动入口增加数据库迁移前置和失败关闭行为。
- `frontend-identity-navigation`: 登录成功后的身份状态必须即时驱动工作台导航。

## Impact

- 修改 `scripts/start-local.ps1`、`frontend/src/App.vue`，复用既有 `scripts/flyway-migrate.ps1`、迁移运行器和身份服务缓存。
- 不变更 API、角色权限矩阵或生产发布流程。
