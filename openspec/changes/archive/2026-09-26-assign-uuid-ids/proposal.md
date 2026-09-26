## Why

当前 MyBatis-Plus 持久化实体使用 `IdType.INPUT`，要求各调用方提前生成 UUID，容易造成主键生成逻辑分散。项目已采用 MyBatis-Plus 3.5.17，现可利用其内置 `ASSIGN_UUID` 能力统一生成 UUID，同时保持 PostgreSQL 现有 `uuid` 主键结构。

## What Changes

- 将现有 MyBatis-Plus 持久化实体的主键策略统一为 `IdType.ASSIGN_UUID`，持久化主键字段改为 `String` 并在映射层转换为领域 `UUID`。
- 配置项目级 UUID 标识生成能力，保持显式主键传入时的兼容性。
- 增加未提供主键时的 Mapper 插入验证和实体策略检查。
- 不引入 Hutool、Leaf 或其他分布式 ID 框架，不修改既有数据库迁移脚本。

## Capabilities

### New Capabilities

- `assign-uuid-ids`: 统一 MyBatis-Plus UUID 主键生成策略。

### Modified Capabilities

<!-- None. Existing repository behavior remains unchanged; this is a persistence implementation capability. -->

## Impact

- 影响 backend 持久化 Entity、MyBatis-Plus 配置和仓储测试。
- 不改变领域层 UUID 类型、API 契约、PostgreSQL 表结构或数据库迁移。
- 不增加 Maven 依赖，继续使用现有 `mybatis-plus-spring-boot4-starter:3.5.17`。
