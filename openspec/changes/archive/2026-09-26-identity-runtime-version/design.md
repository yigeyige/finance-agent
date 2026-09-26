## Context

V2/V8 迁移已建立 `iam.agent_user`、`iam.role_binding` 和 `governance.runtime_bundle`，但 Java 代码只有空模块包，没有数据库访问抽象。项目使用 Java 17、Spring Boot 4.0.4，当前诊断探针直接使用 JDBC，尚未提供 `JdbcTemplate`。任务要求 local/test 合成身份，且生产必须避免测试身份和未发布运行版本。

## Goals / Non-Goals

**Goals:**

- 建立领域不可变对象、状态枚举和仓储接口，隔离 SQL 与业务层。
- 使用参数绑定实现用户、角色绑定和 runtime bundle 的读写及发布态查询。
- 通过显式配置开关提供 local/test 合成用户、角色和一个已发布 runtime bundle 的种子能力。
- 让生产 profile 在配置校验和 Bean 条件上拒绝合成种子。

**Non-Goals:**

- 不新增 HTTP API，不实现任务创建流程或完整认证/SSO 适配器。
- 不自动执行数据库迁移，不在测试启动时强制连接真实 PostgreSQL。
- 不允许通过仓储绕过 runtime bundle 状态校验。

## Decisions

1. **Spring JDBC + `JdbcTemplate`。** 仅引入 `spring-jdbc` 和手工 `DriverManagerDataSource`，避免引入会改变当前启动行为的完整 JDBC starter/DataSource 自动配置；SQL 仍由基础设施层集中管理。
2. **记录/枚举作为领域模型。** 使用 Java 17 `record` 承载不可变快照，状态用枚举映射白名单值；构造器校验必填字段、版本摘要和角色码。
3. **仓储提供发布态查询。** `RuntimeBundleRepository.findPublishedById` 只查询 `status='PUBLISHED'`，后续任务用例必须依赖该方法而不是通用查询，以避免草稿 bundle 被执行。
4. **合成种子显式开关。** 仅在 `local`/`test` profile 注册 `CommandLineRunner`，并要求 `finance-agent.identity.seed-enabled=true`；默认关闭。`prod` 不注册该 Bean，即使误设环境变量也不写入测试数据。
5. **审计上下文显式传入。** 仓储写入方法接收 actor UUID，写入 V8 的 `created_by`/`updated_by`，不从线程隐式推断身份。

## Risks / Trade-offs

- [Risk] 当前运行环境无连接池 → 使用 `DriverManagerDataSource` 仅作为 P0 简化实现，后续仓储规模化时再切换统一连接池。
- [Risk] 直接调用通用 runtime bundle 查询 → 同时提供 `findPublishedById` 并在测试中覆盖草稿/退休拒绝路径。
- [Risk] 合成种子误入生产 → profile 条件、默认关闭和生产启动校验三重限制。
- [Risk] 已有本地库只有直接执行的 V1–V8、没有 Flyway history → 本任务只验证仓储 SQL 和字段，不伪造 history；正式部署仍需 Flyway 流程。

## Migration Plan

1. 添加 JDBC 依赖、数据源配置、领域对象和仓储。
2. 在 local/test 显式开启种子开关后执行一次种子；默认不开启时不写库。
3. 运行仓储契约测试、编译和全量测试；用户自行启动应用验证数据库记录。
4. 若回滚，仅移除新增 Bean/仓储代码；不删除身份、角色或 runtime bundle 数据。

## Open Questions

- SSO subject 到 `agent_user` 的映射及角色授权服务留给后续统一认证任务。
