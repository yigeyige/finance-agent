# Task Scope and Step Model

## Purpose

为调查任务提供版本化范围、步骤审批、并发租约和可恢复工具调用的可审计持久化边界，供后续状态机和运行时复用。
## Requirements
### Requirement: Versioned task scope persistence

系统 SHALL 通过参数化 JDBC 仓储持久化任务、范围修订和前提，并将范围权限字段以带 `schemaVersion` 的 JSONB 对象保存；范围修订 SHALL 只追加，不提供原位更新；只有确认请求才能绑定新的当前范围修订。

#### Scenario: Scope revision is append-only
- **WHEN** 为任务提交新的范围修订
- **THEN** 仓储写入递增 `revisionNo`、范围摘要和 schema 版本，历史修订保持不变

#### Scenario: Invalid scope JSON is rejected
- **WHEN** 对象选择或三类字段权限不是严格合法的 JSON object，或 schema 版本为空
- **THEN** 领域边界拒绝写入，不执行数据库更新

#### Scenario: Valid JSON container is accepted
- **WHEN** 范围 JSON 是严格合法的 JSON object 或 JSON array，且 schema 版本非空
- **THEN** `VersionedJson` 可正常构造并继续进入现有领域校验流程

#### Scenario: Trailing JSON content is rejected
- **WHEN** 范围 JSON 在首个 JSON 值之后包含额外 JSON token 或其他非空内容
- **THEN** 领域边界拒绝该值，不执行数据库更新

#### Scenario: Unconfirmed revision is not current
- **WHEN** 范围请求尚未通过确认应用服务
- **THEN** 系统不更新任务的 `currentScopeRevisionId`，步骤审批和运行路径不能引用该请求

### Requirement: Step approval binding

系统 SHALL 持久化调查步骤和步骤审批，并通过 `(step_id, revision_no)` 唯一关系将每个审批绑定到一个步骤、范围修订和字段/查询策略版本。

#### Scenario: One step has one approval revision
- **WHEN** 同一步骤的相同审批版本被重复写入
- **THEN** 数据库唯一约束拒绝重复记录，原审批历史不被覆盖

#### Scenario: Approval references current scope
- **WHEN** 创建步骤审批
- **THEN** 审批记录必须引用存在的步骤和范围修订，并保存目标摘要、调用上限和过期时间

### Requirement: Lease and optimistic task concurrency

系统 SHALL 支持任务行版本更新和任务租约管理，且一个任务同时最多存在一个未释放租约。

#### Scenario: Optimistic update detects conflict
- **WHEN** 使用过期 `rowVersion` 更新任务
- **THEN** 仓储返回版本冲突且不修改任务

#### Scenario: Second active lease is rejected
- **WHEN** 任务已有未释放租约时再次获取租约
- **THEN** 数据库部分唯一索引拒绝第二个活动租约

### Requirement: Idempotent tool invocation persistence

系统 SHALL 通过工具调用仓储保存审批绑定、请求摘要、响应摘要和状态，并以全局幂等键唯一约束避免重复执行意图。

#### Scenario: Duplicate idempotency key is rejected
- **WHEN** 使用已存在的 `idempotencyKey` 创建调用意图
- **THEN** 数据库拒绝重复记录，调用方可读取原调用状态

#### Scenario: Invocation status is auditable
- **WHEN** 调用状态从 intent 进入运行或完成状态
- **THEN** 仓储保存状态、时间、响应摘要或错误码，且不删除原始调用记录
