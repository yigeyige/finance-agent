## Why

Harness 当前只有数据库迁移，没有 Java 侧的 session、checkpoint、计划修订和 workspace 引用模型，执行状态无法安全序列化、按任务/用户隔离或校验恢复前提。现在补齐这些基础能力，才能让后续 Harness 运行时在崩溃恢复和受控工作区中只保存最小、可追溯引用。

## What Changes

- 新增 `harness.session_state`、`harness.checkpoint`、计划修订和 workspace 引用的领域对象、仓储端口、MyBatis-Plus 实体和显式映射。
- 新增增量迁移补充 checkpoint 的 user/scope 绑定，并建立 `harness.plan_revision` 与 `harness.workspace_ref` 表及唯一约束。
- 提供基于 Jackson 的状态序列化接口；严格拒绝源原响应、凭证、Token 和 `reasoning_content` 等禁止字段进入状态引用。
- 所有状态内容保存 schema version、64 位摘要和最小恢复引用；checkpoint 恢复要求 task、session、step、scope 和 approval 绑定一致。
- 所有跨任务/跨用户查询通过显式 task/user/session 条件隔离。

## Capabilities

### New Capabilities

- `harness-state-checkpoint-model`: Harness session、checkpoint、计划修订、workspace 引用和安全状态序列化。

### Modified Capabilities

<!-- No existing spec-level requirements are modified. -->

## Impact

- 影响 `backend/src/main/java/cn/com/crc/ems/finance/agent/harness/` 的领域、应用和基础设施层。
- 新增 Flyway V9 Harness 迁移，复用现有 Jackson、JSONB 类型处理器、MyBatis-Plus 和 UUID 映射，不引入新依赖。
- 更新 Mapper 扫描、迁移契约、持久化上下文和 UUID 策略测试；不新增 Controller 或运行时启动行为。
