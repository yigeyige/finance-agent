## Context

V3 迁移已经建立 task schema，但当前 Java 工程只有包说明文件，后续状态机和工具网关无法安全复用这些关系。任务范围必须按修订版本追加写入，审批必须绑定步骤与范围，租约需要依靠数据库唯一索引防止并发重复，工具调用需要通过幂等键恢复。

## Goals / Non-Goals

**Goals:**

- 为七张 task 表提供 Java 17 records、状态枚举、仓储端口和 JDBC 实现。
- 用参数化 SQL、显式映射和数据库约束支持查询、追加、乐观更新、租约和幂等调用。
- 将对象选择、时间范围、包含/排除路径和三类字段权限作为带 `schemaVersion` 的 JSONB 文本值保存。

**Non-Goals:**

- 不新增 HTTP API、任务状态机、审批 UI 或工具执行器。
- 不修改 V3/V8 迁移，不自动启动数据库或应用。
- 不在本任务中决定 JSON Schema 的完整业务字段集合；只保存版本和经过边界校验的 JSON 对象文本。

## Decisions

1. **记录型领域模型与字符串枚举**：使用 Java 17 records 承载不可变快照，状态使用枚举并在 JDBC 映射时转换为数据库大写值。相比 JPA Entity，可避免隐式 flush 和懒加载影响状态机。
2. **按能力拆分仓储**：每张表对应最小仓储接口和 JDBC 实现，任务仓储额外提供 `updateWithVersion`，以 `WHERE task_id = ? AND row_version = ?` 检测并发冲突。相比通用 DAO，接口更容易被后续用例替换和测试。
3. **范围只追加不更新**：`ScopeRevisionRepository` 只提供插入与查询，禁止原位更新；任务当前范围指针由任务仓储在同一事务用例中更新。旧修订仍可被审批外键引用。
4. **JSONB 参数绑定**：JSON 文本通过 `SqlParameterValue(Types.OTHER, value)` 绑定，不拼接 SQL；读取使用 `ResultSet#getString`。仓储只校验非空、对象形态和 schema 版本存在，完整 JSON Schema 校验留给后续范围用例。
5. **审批、租约和幂等边界交给数据库**：审批以 `(step_id, revision_no)` 唯一约束，租约依赖 `ux_one_active_lease_per_task`，工具调用依赖 `idempotency_key` 唯一约束；仓储将 `DuplicateKeyException` 保留给应用层转为业务冲突。
6. **时间与审计**：所有模型使用 `Instant`，任务更新携带 `updatedAt/updatedBy`；V8 审计字段由仓储写入。数据库生成字段不通过隐式当前用户推断。

## Risks / Trade-offs

- [Risk] 仅保存 JSON 文本不能替代完整 JSON Schema 校验 → [Mitigation] 明确要求 `schemaVersion` 和 object JSON，后续范围确认用例接入版本化 Schema 校验器。
- [Risk] 任务当前范围指针与范围插入分两次 SQL 可能被错误拆开 → [Mitigation] 仓储暴露清晰的更新条件，后续用例使用 Spring 事务在同一事务内完成。
- [Risk] 租约和幂等冲突依赖 PostgreSQL 约束异常 → [Mitigation] 保留 SQLState/根因并在应用层映射为明确冲突，不吞异常。

## Migration Plan

不新增迁移。部署时确保 V1-V8 已完成，再发布 Java 类；回滚仅回滚应用版本，保留已写入的 task 数据和约束。

## Open Questions

- 具体业务 JSON Schema 的版本目录和校验器由后续范围确认任务决定。
- 任务状态流转和审批过期策略由 DEV-RUN 任务实现。
