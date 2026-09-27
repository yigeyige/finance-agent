## Context

`V5__packet_and_governance.sql` 已创建审计和 outbox 表，`V8__audit_fields.sql` 已补充统一审计字段，但当前 Java 代码没有对应的领域对象和仓储。后续发布事务需要在同一个数据库事务中写入业务记录、审计事件和本地 outbox，因此必须先固定事件的规范化内容、摘要链和幂等边界。

## Goals / Non-Goals

**Goals:**

- 让审计事件只能通过追加接口写入，并可根据固定字段重算 payload/event digest。
- 以 `prev_event_digest` 串联同一任务的审计事件，检测乱序或篡改。
- 以唯一 `idempotency_key` 防止 outbox 重复写入，并提供待投递查询与投递结果更新。
- 通过 Spring `@Transactional` 应用端口保证审计和 outbox 写入共享本地事务。
- 使用现有 Jackson、MyBatis-Plus、JDK SHA-256 和 JSONB 类型处理能力。

**Non-Goals:**

- 不实现独立审计系统、消息代理、后台投递线程或外部网络调用。
- 不改造已有发布 API 或任务状态机；后续任务通过本任务提供的端口接入。
- 不引入新的 JSON、加密或消息中间件依赖。

## Decisions

- **规范化与摘要**：使用项目已有 Jackson 严格解析能力，并在审计边界按排序字段输出紧凑 JSON；payload digest 对规范化 payload 求 SHA-256，event digest 对事件固定字段串求 SHA-256。相比直接使用输入字符串，这能使等价 JSON 得到同一摘要并支持重算。
- **追加链范围**：以 `task_id` 作为链范围，仓储提供按任务读取最新事件的查询；首个事件的 `prev_event_digest` 为空。相比全局链，任务级链避免无关任务之间形成写入热点。
- **幂等边界**：outbox 使用数据库唯一 `idempotency_key`；重复写入通过查询返回已有记录，数据库唯一约束作为并发兜底。相比仅在 Java 内存去重，该方案可跨实例工作。
- **事务边界**：新增 `AuditOutboxTransaction` 应用端口实现使用 `@Transactional`，先写审计再写 outbox；任一 Mapper 异常向上抛出，由 Spring 回滚整个本地事务。投递状态更新不修改审计事件。
- **不可变保护**：V10 增加审计表的数据库触发器，拒绝 UPDATE/DELETE；Java 审计仓储只暴露 append/find 方法。相比只依赖代码约定，数据库保护可覆盖误用 SQL 的路径。

## Risks / Trade-offs

- [Risk] 旧环境中已有 outbox 行没有幂等键 → V10 以 `outbox_id` 回填，随后设置非空并建立唯一约束；新写入必须提供业务幂等键。
- [Risk] 规范化规则变化会影响摘要 → 事件记录 `canonicalization_version`，当前固定为 `audit-v1`，后续变更通过新版本兼容处理。
- [Risk] 触发器阻止运维修复历史审计 → 审计按追加设计，修复通过补偿事件表达，不开放原地修改。

## Migration Plan

1. 执行 V10，补充 outbox 幂等键/约束、投递索引、摘要检查约束和审计不可变触发器。
2. 发布后端实体、Mapper、仓储和事务端口；既有表数据可按 `outbox_id` 回填幂等键。
3. 使用契约测试验证摘要、链、幂等和失败回滚；用户再执行本地数据库事务人工验证。
4. 回滚仅允许回滚应用版本；数据库迁移采用前向补偿，不删除已产生的审计记录。

## Open Questions

- 外部审计系统或消息代理的投递协议留待后续任务确定；本 change 只提供本地 outbox 存储。
