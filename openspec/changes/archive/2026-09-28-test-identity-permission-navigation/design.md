## Context

IAM 领域已经有 `AgentUser`、`RoleBinding` 和 local/test 种子，但 Web 层尚未建立当前身份上下文，任务接口仍接收 `X-Actor-User-Id`，无法证明请求来源。项目未引入 Spring Security，本任务需要在不增加依赖的约束下完成可验证的测试身份和权限边界。

## Goals / Non-Goals

**Goals:**

- 在 local/test 中通过受控身份标识解析三类合成用户，并提供当前身份接口。
- 生产环境只接受容器提供的 `Principal`，测试身份头和种子开关不能启动或生效。
- 将任务写操作和配置管理映射到角色权限，在服务端拒绝越权请求。
- 前端读取权限矩阵显示导航，身份切换仅在 local/test 出现。

**Non-Goals:**

- 不实现真实 SSO/OIDC、JWT 签发或账号管理。
- 不改变角色持久化模型和生产认证供应商。
- 不让前端权限判断替代服务端鉴权。

## Decisions

1. **使用 Servlet `Principal` + 受控测试标识。** 生产从请求 `Principal` 解析认证主体；local/test 允许 `X-Test-Identity` 仅取 `operator`、`viewer`、`platform-admin` 三个白名单值，并通过仓储加载实际角色。这样不新增 Spring Security 依赖，也不会把任意用户 ID 当作身份。
2. **ThreadLocal 请求上下文。** `IdentityContextFilter` 在请求开始解析身份并在 finally 清理；控制器通过 `CurrentIdentity` 访问，不从请求体或自定义 actor ID 取身份。
3. **服务端守卫按路径和角色执行。** `PermissionGuardInterceptor` 对任务写入要求 OPERATOR，对工具目录管理要求 PLATFORM_ADMIN；VIEWER 只允许公开读取路径。规则集中定义，前端仅消费同一权限列表。
4. **前端 localStorage 保存测试身份选择。** API 客户端为同源请求附带 `X-Test-Identity`，生产构建不会展示切换入口；服务端仍是最终鉴权点。

## Risks / Trade-offs

- [测试标识是请求头] → 仅在 local/test profile 和白名单值生效，生产忽略并要求 Principal。
- [没有 Spring Security 的完整认证链] → 明确保留 `Principal` 接入点，后续 SSO 适配只替换解析器，不扩散到业务控制器。
- [旧客户端发送 actor 头] → 兼容读取但校验其与当前上下文一致；缺少上下文的写请求直接拒绝。

## Migration Plan

先引入上下文、身份接口和拦截器，再将任务控制器改为读取当前身份；最后接入前端切换入口。local/test 手动验证三种身份，生产执行启动负向测试；回滚时可移除拦截器注册而不改数据库。

## Open Questions

- 真实 SSO 接入时由部署侧提供何种 `Principal` 名称格式，留待认证适配任务确定。
