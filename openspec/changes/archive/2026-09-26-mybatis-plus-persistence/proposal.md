## Why

项目技术基线已固定 MyBatis-Plus 3.5.17，但当前身份、治理和任务持久层仍直接依赖 `JdbcTemplate`，导致普通 CRUD 与复杂 SQL 的实现方式不一致，也无法使用统一的 Mapper、实体映射和后续分页/乐观锁扩展。现在迁移可以在更多业务模块落地前统一持久层边界。

## What Changes

- 引入 `mybatis-plus-spring-boot4-starter` 3.5.17，并配置 Mapper 扫描和数据源集成。
- 将 IAM、治理和 task 模块的 `JdbcTemplate` 仓储改为 MyBatis-Plus Mapper、持久化实体和领域映射。
- 保留 JSONB 类型处理、已发布运行版本过滤、角色撤销、任务乐观版本、活动租约和工具调用幂等行为。
- 移除业务仓储对 `JdbcTemplate` 与 RowMapper 的直接依赖，更新相关契约测试和持久层说明。

## Capabilities

### New Capabilities

- `mybatis-plus-persistence`: 统一基于 MyBatis-Plus Mapper 的身份、运行版本和任务数据访问边界。

### Modified Capabilities

<!-- No existing requirement-level capability is modified. -->

## Impact

- 影响 `backend/pom.xml`、数据源/MyBatis 配置、iam/governance/task 基础设施实现和测试。
- 不修改现有 V1-V8 数据库迁移、不改变领域仓储接口和业务状态约束。
- 可能移除显式 `spring-jdbc` 使用；数据库连接仍由统一 DataSource 提供。
