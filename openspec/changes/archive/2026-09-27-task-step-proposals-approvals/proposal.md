## Why

任务范围确认后，系统还缺少可审计的步骤提议和人工审批边界，无法把 Agent 的下一步动作、数据来源和字段策略冻结为可验证的执行授权。现在补齐这一层，可以在后续工具调用前拒绝旧范围、过期审批、模板漂移和字段扩大，同时保留有限的重试能力。

## What Changes

- 保存一个推荐步骤和最多两个备选步骤，步骤提议仅引用当前已确认范围。
- 新增审批卡应用服务，绑定目标摘要、来源、范围修订、字段策略、查询模板、调用上限和有效期。
- 审批采用不可变 revision；相同绑定只能追加同一 revision，任一绑定改变必须生成新审批。
- 新增服务端内部短期 `ExecutionPermit` 和业务守卫；permit 不进入 REST 响应、浏览器或模型上下文。
- 提供步骤提议、审批卡和审批撤销的版本化接口及 JSON Schema/样例。

## Capabilities

### New Capabilities

- `task-step-proposal-approval`: 任务步骤提议、审批绑定、内部 permit 和执行前守卫。

### Modified Capabilities

- `task-scope-step-model`: 补充步骤提议数量、审批 revision 绑定和当前范围一致性要求。

## Impact

- 后端 task application/domain/infrastructure/web 层新增步骤提议、审批服务、守卫和 DTO。
- 复用现有 `investigation_step`、`step_approval` 表、MyBatis-Plus Mapper、审计链、UTC Clock、UUID 和 SHA-256 基础能力，不新增外部依赖或数据库迁移。
- 更新 OpenAPI、JSON Schema、契约样例和自动化测试；不启动本地服务，不执行真实源系统调用。
