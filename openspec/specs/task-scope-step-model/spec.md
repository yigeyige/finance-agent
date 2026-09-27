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

系统 SHALL 持久化调查步骤和步骤审批，并通过 `(step_id, revision_no)` 唯一关系将每个审批绑定到一个步骤、当前范围修订和字段/查询策略版本；审批只能追加，不能覆盖历史记录。

#### Scenario: One step has one approval revision
- **WHEN** 同一步骤的相同审批版本被重复写入
- **THEN** 数据库唯一约束拒绝重复记录，原审批历史不被覆盖

#### Scenario: Approval references current scope
- **WHEN** 创建步骤审批
- **THEN** 审批记录必须引用存在的步骤和范围修订，并保存目标摘要、调用上限和过期时间

#### Scenario: Changed binding requires a new approval revision
- **WHEN** 目标、来源、范围、字段策略、查询模板或调用上限任一绑定发生改变
- **THEN** 应用服务追加新的审批 revision，不原位更新旧审批

### Requirement: Lease and optimistic task concurrency

系统 SHALL 支持任务行版本更新和任务租约管理，且一个任务同时最多存在一个未释放租约。租约只能绑定当前正在执行的步骤；等待人工确认、未获批或终止状态的任务不得持有活动租约。

#### Scenario: Optimistic update detects conflict
- **WHEN** 使用过期 `rowVersion` 更新任务
- **THEN** 仓储返回版本冲突且不修改任务

#### Scenario: Second active lease is rejected
- **WHEN** 任务已有未释放租约时再次获取租约
- **THEN** 数据库部分唯一索引拒绝第二个活动租约

#### Scenario: Human waiting task has no lease
- **WHEN** 任务等待步骤审批或专业确认
- **THEN** 任务不创建活动租约，Worker 轮询不会占用该任务的执行槽位

### Requirement: Idempotent tool invocation persistence

系统 SHALL 通过幂等服务和工具调用仓储保存审批绑定、请求摘要、响应摘要、错误码及状态。幂等键 SHALL 由 `taskId + stepId + approvalRevision + invocationNo` 按固定规范化格式生成，并受全局唯一约束；工具执行前必须先记录 `INTENT_RECORDED` 意图，状态转换必须按期望旧状态条件更新。

#### Scenario: First request records an intent

- **WHEN** 使用新的任务、步骤、审批修订和调用序号请求工具调用
- **THEN** 系统生成稳定幂等键并持久化 `INTENT_RECORDED` 记录，随后才允许进入工具执行

#### Scenario: Duplicate request replays a completed result

- **WHEN** 使用已存在幂等键再次请求，且原记录状态为 `SUCCEEDED`、`PARTIAL` 或 `FAILED`
- **THEN** 系统返回原调用记录及已持久化的响应摘要/错误码，不创建第二条记录或再次执行工具

#### Scenario: In-flight duplicate does not execute twice

- **WHEN** 使用已存在幂等键再次请求，且原记录状态为 `INTENT_RECORDED` 或 `RUNNING`
- **THEN** 系统返回进行中结果，不创建第二条记录、不覆盖原状态且不发起第二次工具调用

#### Scenario: Unknown result requires reconciliation

- **WHEN** 原调用状态为 `UNKNOWN`，再次收到相同幂等键请求
- **THEN** 系统返回人工核对动作和原记录，不自动重试或产生新的执行意图

#### Scenario: Concurrent duplicate insertion is resolved

- **WHEN** 两个请求同时使用同一幂等键创建调用意图
- **THEN** 只有一条记录成功写入，竞争方读取并返回该记录的当前状态

#### Scenario: Invalid state transition is rejected

- **WHEN** 已处于终态的调用尝试被更新为其他状态，或状态更新的旧状态不匹配
- **THEN** 仓储不修改记录，服务返回确定的状态冲突，原始审计记录保持不变

#### Scenario: Invocation result is auditable

- **WHEN** 调用从意图进入运行或完成状态
- **THEN** 仓储保存状态、时间、响应摘要或错误码及审计字段，且不删除原始调用记录
