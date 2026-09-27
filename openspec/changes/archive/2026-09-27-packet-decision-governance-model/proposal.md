## Why

当前 packet 模块只有数据库迁移，缺少可追溯的决策包草稿、复核、发布修订和交接领域模型，无法在应用层表达“JSON 权威、HTML 快照”“复核绑定准确内容”和“已发布修订不可变”等发布治理约束。现在补齐这些基础模型，才能让后续发布事务、查询权限和工作台能力建立在稳定的版本链上。

## What Changes

- 新增 `packet_draft`、`packet_review`、`packet_revision` 和 `handoff` 的领域对象、仓储端口、MyBatis-Plus 持久化实体及显式映射。
- 复用既有 JSONB 类型处理器保存规范化 JSON；领域层校验内容摘要、范围版本、运行版本和发布状态。
- 为同一任务建立唯一修订号约束；更正通过新修订和 `supersedes_revision_id` 表达，不提供已发布修订的更新或删除仓储操作。
- 提供只查询已发布修订的仓储入口，阻止 VIEWER 读取草稿或未发布内容。
- 为复核准确 digest、范围和 runtime bundle，以及交接与发布修订绑定补充领域守卫和契约测试。

## Capabilities

### New Capabilities

- `packet-decision-governance-model`: 决策包草稿、复核、不可变发布修订和交接模型。

### Modified Capabilities

<!-- No existing spec-level requirements are modified. -->

## Impact

- 影响 `backend/src/main/java/cn/com/crc/ems/finance/agent/packet/` 下的领域、应用和基础设施层。
- 更新 MyBatis Mapper 扫描边界和持久化上下文契约测试；复用已存在的 `V5__packet_and_governance.sql` 与 `V8__audit_fields.sql`，不新增依赖或重复迁移。
- 不新增 API；后续 API/发布用例将使用本任务提供的仓储端口和领域对象。
