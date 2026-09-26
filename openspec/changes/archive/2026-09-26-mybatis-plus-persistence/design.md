## Context

当前 IAM、governance 和 task 基础设施仓储全部通过 `JdbcTemplate` 手写 SQL 和 RowMapper。技术基线已经固定 `mybatis-plus-spring-boot4-starter` 3.5.17，后续普通 CRUD、条件查询和分页需要统一使用 Mapper 层；同时 PostgreSQL 的 schema-qualified table、JSONB、部分唯一索引和乐观版本更新仍需保留显式 SQL 能力。

## Goals / Non-Goals

**Goals:**

- 引入 MyBatis-Plus 3.5.17 Spring Boot 4 starter、DataSource 和 Mapper 扫描配置。
- 为现有领域仓储建立持久化 Entity、Mapper 和显式领域映射，保留原仓储接口。
- 使用 `BaseMapper` 复用单表 CRUD；用 Mapper 注解 SQL 集中表达 published-only、乐观版本、租约、角色撤销和幂等更新。
- 为 PostgreSQL JSONB 增加参数化 TypeHandler，避免把 JSON 作为字符串拼接进 SQL。

**Non-Goals:**

- 不改变领域对象、仓储接口、数据库迁移和业务状态机行为。
- 不新增 HTTP API、分页接口或事务编排；事务边界留给后续应用用例。
- 不批量改造尚未存在的其他持久层模块。

## Decisions

1. **使用 Boot 4 专用 starter**：固定 `com.baomidou:mybatis-plus-spring-boot4-starter:3.5.17`，显式 `@MapperScan` 指向三个基础设施 mapper 包。相比手工组装 MyBatis，starter 能统一 Boot 4 生命周期和配置绑定。
2. **Entity 与领域 record 分离**：MyBatis-Plus Entity 仅承担列映射和持久化可变属性，仓储负责 Entity↔Domain 显式转换，避免数据库结构泄漏到应用/API。
3. **BaseMapper + 集中 SQL**：常规按 ID、插入和更新复用 `BaseMapper`；跨状态或部分索引操作用 Mapper 的 `@Select/@Insert/@Update`，所有值仍通过 `#{}` 参数绑定。相比把 SQL 放在仓储类，Mapper 层更容易做 SQL 契约测试。
4. **schema-qualified table name**：Entity 使用 `@TableName("schema.table", autoResultMap = true)`，复杂查询在注解 SQL 中明确 schema，避免依赖默认 search path。
5. **JSONB TypeHandler**：范围实体的 JSON 字段使用 PostgreSQL `PGobject` 写入 `jsonb`，读取为字符串，再由领域 `VersionedJson` 校验；不使用字符串拼接或隐式 cast。
6. **保留 DataSource、移除 JdbcTemplate**：数据源改为 Hikari 配置，仅保留 MyBatis-Plus 使用需要的 DataSource，删除 `JdbcTemplate` bean、直接依赖和 RowMapper 实现。

## Risks / Trade-offs

- [Risk] Entity 字段与迁移列不一致会在运行时失败 → [Mitigation] 为每个实体补充列契约测试，完整测试校验 Mapper 上下文可加载。
- [Risk] JSONB TypeHandler 依赖 PostgreSQL 驱动类型 → [Mitigation] 将 PGobject 封装在基础设施层，并以 PostgreSQL 驱动 42.7.11 验证。
- [Risk] MyBatis-Plus 自动 SQL 对 schema 名称和复合业务键支持有限 → [Mitigation] 仅在单表 CRUD 使用 BaseMapper，复杂条件固定写在 Mapper SQL 中。

## Migration Plan

先加入 starter 和 Mapper 配置，再按 IAM、governance、task 顺序迁移 Entity/Mapper/Repository，最后删除 JdbcTemplate 配置和旧实现。迁移不执行数据库写入；回滚应用时恢复旧仓储类即可，数据库结构无需回滚。

## Open Questions

- 后续如启用分页，需单独评估 MyBatis-Plus 分页插件和接口契约，不在本任务中引入。
