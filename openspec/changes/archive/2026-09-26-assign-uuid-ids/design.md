## Context

当前 backend 已使用 MyBatis-Plus 3.5.17，所有持久化 Entity 的 UUID 主键仍标记为 `IdType.INPUT`。这要求仓储或调用方在插入前手动准备主键。数据库 V1-V8 迁移、领域对象和 API 契约均以 PostgreSQL `uuid` 为基础，本次只调整生成策略，不改变字段类型或迁移历史。

## Goals / Non-Goals

**Goals:**

- 统一所有 MyBatis-Plus Entity 的 UUID 主键策略为 `IdType.ASSIGN_UUID`，并将持久化主键属性声明为 `String` 以匹配 MyBatis-Plus 内置生成器返回值。
- 保持 UUID Java 类型、PostgreSQL `uuid` 类型和现有领域仓储接口不变。
- 通过 MyBatis-Plus 内置生成能力完成默认主键赋值，并验证显式主键兼容路径。

**Non-Goals:**

- 不迁移为雪花 Long/bigint，不修改数据库迁移脚本。
- 不新增 Hutool、Leaf、Redis 或其他分布式 ID 框架。
- 不改变 API、领域对象字段类型、权限或事务行为。

## Decisions

1. **使用 `IdType.ASSIGN_UUID`**：MyBatis-Plus 3.5.17 已提供 UUID 主键自动分配能力，默认 `nextUUID` 返回无连字符的 `String`。持久化 Entity 的主键属性改为 `String`，领域映射层负责与 `UUID` 转换，数据库列仍保持 PostgreSQL `uuid`；数据源将 PostgreSQL JDBC `stringtype` 设为 `unspecified`，让 UUID 列按目标列类型接收字符串参数。
2. **不新增全局外部生成器依赖**：本次以 MyBatis-Plus 内置 `IdentifierGenerator` 为统一实现。若后续需要跨服务可排序 ID，再单独评估 worker 分配和外部协调机制。
3. **保留显式主键优先级**：调用方显式设置 UUID 时，映射为字符串后继续使用该值；仅在主键未提供时由 `ASSIGN_UUID` 自动生成，避免破坏种子数据和幂等写入。
4. **以实体级注解作为约束入口**：逐一检查所有持久化 Entity 的 `@TableId`，不通过隐式命名约定覆盖已有表字段映射。

## Risks / Trade-offs

- [Risk] MyBatis-Plus 的自动赋值只在经过其 Mapper 插入流程时生效 → 保留实体/Mapper 测试，并禁止在 Service 或 Controller 直接绕过 Mapper 写库。
- [Risk] 迁移遗漏某个 Entity 会导致生成策略不一致 → 增加静态检查或测试，扫描持久化 Entity 的 `@TableId` 配置。
- [Risk] 外部调用方依赖预生成 UUID → 保留显式 UUID 兼容路径，转换仅发生在持久化映射层，不改变接口字段和数据库类型。

## Migration Plan

1. 将所有持久化 Entity 的主键注解改为 `IdType.ASSIGN_UUID`。
2. 增加无主键插入和显式主键插入测试，确认 UUID 类型和领域映射不变。
3. 执行编译、相关测试、完整测试和 OpenSpec 校验。
4. 回滚时仅需恢复 Entity 注解为 `IdType.INPUT`；不涉及数据库迁移回滚。

## Open Questions

- 当前无待确认事项；跨服务统一 ID 或可排序 ID 另行立项。
