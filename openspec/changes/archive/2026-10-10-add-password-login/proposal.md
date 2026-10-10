## Why

当前工作台依赖 local/test 请求头或外部 Principal，不提供业务人员可使用的登录和退出入口。内网部署需要以账号密码建立受控会话，并通过默认管理员完成首次配置。

## What Changes

- 新增账号密码登录、退出登录和服务端内存会话，使用 HttpOnly Cookie 传递不透明会话标识。
- 新增登录页、未登录路由跳转、右上角用户下拉菜单及退出入口，移除前端测试身份切换入口。
- 增加密码凭据表与前向 Flyway 迁移，幂等初始化 `admin` 平台管理员及其密码散列。
- 保持既有角色权限矩阵和业务接口授权策略不变。

## Capabilities

### New Capabilities

- `password-login-session`: 账号密码认证、会话生命周期、默认管理员初始化和登录接口安全边界。

### Modified Capabilities

- `frontend-identity-navigation`: 前端从测试身份切换改为登录态路由保护、用户下拉和退出交互。
- `database-schema-comments`: 新增 IAM 密码凭据表和字段的中文数据库注释。

## Impact

- 受影响代码：IAM 应用、持久化、Web 接口、数据库迁移与注释校验、前端身份服务、路由、根布局和登录页。
- 新增 API：`POST /api/v1/identity/login`、`POST /api/v1/identity/logout`。
- 不引入新的第三方安全框架或前端依赖。
