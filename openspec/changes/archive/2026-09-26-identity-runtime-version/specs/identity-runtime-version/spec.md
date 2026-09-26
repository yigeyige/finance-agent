## ADDED Requirements

### Requirement: Identity and role persistence

系统 SHALL 通过 Spring JDBC 仓储持久化 `iam.agent_user` 和 `iam.role_binding`，并以参数绑定支持按用户 ID、auth subject、人员编号查询以及角色授予和撤销追踪。

#### Scenario: Unique identity lookup
- **WHEN** 使用 auth subject 或 employee number 查询用户
- **THEN** 仓储返回唯一用户；重复值由数据库唯一约束拒绝

#### Scenario: Role revocation is traceable
- **WHEN** 撤销用户角色
- **THEN** 仓储保留原授予记录并设置撤销时间，不删除历史授权记录

### Requirement: Published runtime bundle access

系统 SHALL 持久化 `governance.runtime_bundle` 的应用、AgentScope、模型、提示、规则、工具和源目录版本，并 SHALL 提供只返回 `PUBLISHED` 状态的查询能力。

#### Scenario: Published bundle can be resolved
- **WHEN** 按 runtime bundle ID 查询且状态为 `PUBLISHED`
- **THEN** 仓储返回完整版本快照，供任务用例绑定

#### Scenario: Unpublished bundle is rejected
- **WHEN** 按 runtime bundle ID 查询且状态为 `DRAFT`、`TESTED` 或 `RETIRED`
- **THEN** 发布态查询返回空结果，调用方不能使用该 bundle 创建可运行任务

### Requirement: Environment-scoped synthetic identities

系统 SHALL 仅在 `local` 或 `test` profile 且显式开启种子开关时提供 OPERATOR、VIEWER、PLATFORM_ADMIN 合成用户/角色和已发布 runtime bundle；prod profile SHALL 不注册或执行该种子组件。

#### Scenario: Local seed is explicitly enabled
- **WHEN** active profile 为 `local`/`test` 且 `finance-agent.identity.seed-enabled=true`
- **THEN** 种子组件幂等创建合成用户、角色绑定和一个可查询的已发布 runtime bundle

#### Scenario: Production rejects synthetic seed
- **WHEN** active profile 为 `prod`，无论种子开关取值如何
- **THEN** 合成种子 Bean 不注册，数据库不写入测试身份
