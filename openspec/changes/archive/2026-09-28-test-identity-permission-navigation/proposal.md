## Why

P0 工作台已有可重复的三类合成身份和角色数据，但请求仍依赖调用方自行传入操作者标识，前端也没有统一的身份切换和权限导航边界。现在补齐 local/test 身份适配、请求身份上下文和服务端权限守卫，才能让人工验证真实覆盖 OPERATOR、VIEWER 与 PLATFORM_ADMIN 的差异。

## What Changes

- 增加 local/test 专用的合成身份选择入口和身份查询接口。
- 从请求安全上下文解析当前用户与角色，业务写接口不再信任页面传入的操作者身份。
- 建立角色到导航能力的权限矩阵，服务端拒绝越权请求，前端只用于显示导航。
- 生产启动继续禁止测试身份和种子开关。

## Capabilities

### New Capabilities

- `test-identity-context`: local/test 合成身份适配、当前身份查询和请求上下文。
- `role-permission-guard`: 三类角色的权限矩阵及服务端守卫。
- `frontend-identity-navigation`: 前端身份切换入口、权限导航和请求身份传递。

### Modified Capabilities

## Impact

- 影响 backend IAM/web 过滤器、任务控制器、错误响应和 local/test 配置。
- 影响 frontend API 请求头、路由元数据、App 导航和工作台身份入口。
- 不新增 Spring Security 或其他框架依赖；复用现有 Spring Web、IAM 仓储和 Vue 2.7.16。
