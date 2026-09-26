## Why

数据库中的身份和运行版本表已经存在，但后端尚无类型化领域对象、参数化仓储或受控的测试身份入口，后续任务无法安全引用用户、角色和已发布 runtime bundle。现在补齐这一层，可以冻结运行版本链并为任务状态机提供稳定的持久化边界。

## What Changes

- 新增 `agent_user`、`role_binding`、`runtime_bundle` 的 Java 领域对象、状态枚举和 Spring JDBC 仓储接口/实现。
- 提供按 auth subject、人员编号和用户 ID 查询身份的参数化仓储能力，支持角色授予/撤销追踪。
- 提供 runtime bundle 保存、查询和“仅已发布”查询，禁止业务层把草稿或已退休 bundle 当作可运行版本。
- 增加仅 local/test profile 且显式开关控制的合成身份种子组件，prod 不加载或写入合成用户。
- 补充仓储契约测试、生产配置负向测试和数据库迁移/字段验证说明。

## Capabilities

### New Capabilities

- `identity-runtime-version`: 提供身份、角色绑定和运行版本的领域与 Spring JDBC 持久化能力。

### Modified Capabilities

- 无。

## Impact

- 影响 `iam`、`governance` 模块、共享 JDBC 配置、后端 Maven 依赖、local/test 配置和测试。
- 新增 `spring-jdbc` 依赖及数据库连接数据源 Bean，但不在应用启动时自动执行 Flyway 或自动写入种子数据。
- 生产环境仅允许真实身份和已发布 runtime bundle；合成种子开关在 prod profile 下不可生效。
