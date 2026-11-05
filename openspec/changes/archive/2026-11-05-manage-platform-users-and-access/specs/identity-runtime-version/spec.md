## MODIFIED Requirements

### Requirement: Environment-scoped synthetic identities
系统 SHALL 仅在 `local` 或 `test` profile 且显式开启种子开关时提供 PLATFORM_USER、PLATFORM_ADMIN 合成用户/角色和已发布 runtime bundle；prod profile SHALL 不注册或执行该种子组件。系统角色仅为 PLATFORM_USER、PLATFORM_ADMIN。

#### Scenario: Local seed is explicitly enabled
- **WHEN** active profile 为 `local`/`test` 且 `finance-agent.identity.seed-enabled=true`
- **THEN** 种子组件幂等创建两种角色的合成用户、角色绑定和一个可查询的已发布 runtime bundle

#### Scenario: Production rejects synthetic seed
- **WHEN** active profile 为 `prod`，无论种子开关取值如何
- **THEN** 合成种子 Bean 不注册，数据库不写入测试身份

## ADDED Requirements

### Requirement: Current role and status authorization
系统 MUST 在每次受保护请求使用当前 ACTIVE 状态及活动角色授权，不得仅依赖登录时缓存身份。

#### Scenario: Disabled session
- **WHEN** 已登录用户被停用后继续请求受保护接口
- **THEN** 请求按无有效身份拒绝
