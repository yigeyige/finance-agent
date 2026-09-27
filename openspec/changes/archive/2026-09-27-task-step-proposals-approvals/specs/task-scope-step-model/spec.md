## MODIFIED Requirements

### Requirement: Step approval binding

系统 SHALL 持久化调查步骤和步骤审批，并通过 `(step_id, revision_no)` 唯一关系将每个审批绑定到一个步骤、当前范围修订和字段/查询策略版本；审批只能追加，不能覆盖历史记录。

#### Scenario: One step has one approval revision

- **WHEN** 同一步骤的相同审批版本被重复写入
- **THEN** 数据库唯一约束拒绝重复记录，原审批历史不被覆盖

#### Scenario: Approval references current scope

- **WHEN** 创建步骤审批
- **THEN** 审批记录必须引用存在且为任务当前绑定的步骤、范围修订，并保存目标摘要、调用上限和过期时间

#### Scenario: Changed binding requires a new approval revision

- **WHEN** 目标、来源、范围、字段策略、查询模板或调用上限任一绑定发生改变
- **THEN** 应用服务追加新的审批 revision，不原位更新旧审批
