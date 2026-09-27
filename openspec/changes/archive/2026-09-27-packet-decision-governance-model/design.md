## Context

V5 已创建 `packet.packet_draft`、`packet.packet_review`、`packet.packet_revision` 和 `packet.handoff`，V8 为其补充审计字段，但 `packet` Java 模块目前只有包说明文件。任务、范围、证据和运行版本模型已使用 Java record、显式 Entity/Domain 映射和 MyBatis-Plus `BaseMapper`，本任务需要保持同一分层和 ID 策略。

## Goals / Non-Goals

**Goals:**

- 建立四个 packet 聚合对象及受控状态/复核枚举。
- 以 `VersionedJson` 表示规范 JSON，复用既有 `JsonbTypeHandler` 写入 PostgreSQL JSONB。
- 通过领域守卫保证复核与草稿的 digest、范围修订和 runtime bundle 一致。
- 通过只追加的 revision 仓储端口表达已发布修订不可变，并提供仅返回已发布修订的查询。
- 对 V5/V8 结构、唯一约束、JSON 权威和仓储映射提供可执行契约测试。

**Non-Goals:**

- 不新增数据库迁移、依赖、Controller、发布事务编排或 Viewer API。
- 不在本任务实现 HTML 渲染器、canonical JSON 摘要生成器或真实用户/权限认证；这些由后续任务复用或实现。
- 不通过数据库触发器实现不可变性；应用仓储不暴露更新/删除方法，数据库唯一约束负责版本并发底线。

## Decisions

1. **四个独立领域记录与仓储端口。** 每张表使用独立 record、Entity、Mapper 和 Repository，避免把草稿、复核、发布和交接混成上帝对象；替代方案是单一 Packet aggregate，但会掩盖四张表不同生命周期和写入边界。
2. **JSON 统一使用 `VersionedJson`。** `canonicalContent`、`sourceVersions` 和 `coverageSummary` 在进入领域层时要求合法 JSON，并保留 schema version；持久化复用现有 `JsonbTypeHandler`。不引入新的 JSON 库或手写 JSON parser。
3. **复核一致性显式守卫。** `PacketReview.requireMatches(PacketDraft)` 比较 digest、scope revision 和 runtime bundle，并要求草稿状态可复核；拒绝只比较 review 自身字段的弱校验。
4. **发布修订只追加。** `PacketRevisionRepository` 只提供 `insert`、按 ID 查询和 `findPublishedByTaskId`；不提供 update/delete。revision record 要求发布者、发布时间、快照 JSON 和 digest 完整，并支持 `supersedesRevisionId` 表达更正链。
5. **权限查询入口最小化。** VIEWER 场景使用 `findPublishedByTaskId`，Mapper 查询固定 `published_at IS NOT NULL`（revision 表每行均为发布快照）并按 revision 降序；草稿和 review 不暴露给该端口。
6. **ID 与映射沿用现有基线。** 单表主键 Entity 使用 `IdType.ASSIGN_UUID` + `String`，映射层转换为领域 `UUID`；审计字段保持 `UUID/Instant`，枚举保存数据库字符串。

## Risks / Trade-offs

- [Risk] 领域层接收的 digest 仍由调用方计算，当前任务不提供 canonicalization 算法。→ [Mitigation] 强制 64 位摘要非空并绑定 review/revision；DEV-DATA-008 提供统一摘要工具后再替换调用方。
- [Risk] 只靠仓储端口阻止更新/删除，绕过应用直接操作数据库仍可能修改已发布行。→ [Mitigation] 当前任务补充仓储契约与 schema 唯一约束检查；后续事务/权限任务增加数据库权限和负向 SQL 验证。
- [Risk] `html_snapshot` 可能与 JSON 内容不一致。→ [Mitigation] revision 同时保存 canonical JSON、HTML 和同一 content digest，后续发布用例在生成快照时执行一致性检查。

## Migration Plan

无需新增迁移。部署前确认 V5/V8 已执行；回滚时只需回滚应用代码，保留已存在的 packet 表和历史数据。

## Open Questions

- HTML 快照渲染器、canonical JSON 摘要生成和 Viewer API 的具体实现留给后续任务。
