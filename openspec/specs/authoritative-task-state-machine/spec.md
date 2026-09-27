## Purpose

固化调查任务的权威状态转移、审批边界、乐观版本更新和状态审计事件，确保任务状态只能通过受控应用服务推进。

## Requirements

### Requirement: Authoritative task state transitions

系统 SHALL 通过纯领域状态机和应用服务执行任务状态转移；状态改变必须校验当前状态、显式原因和必要的恢复目标。

#### Scenario: Normal lifecycle follows the transition table

- **WHEN** 应用服务请求附件 A 定义的正常相邻状态转移
- **THEN** 状态机接受转移并返回带 from/to/trigger 的转移结果

#### Scenario: Invalid transition is rejected before persistence

- **WHEN** 请求不在状态转移表中或当前状态已是终态
- **THEN** 状态机抛出明确状态异常，任务仓储和状态事件仓储均不被调用

#### Scenario: Pause and resume retain approval boundaries

- **WHEN** 任务因模型、预算或源不可用暂停后请求恢复
- **THEN** 只有显式允许的恢复目标被接受，恢复不能跳过步骤审批

#### Scenario: Cancellation is explicit

- **WHEN** 授权调用对非终态任务发起取消
- **THEN** 必须记录非空取消原因并转为 `CANCELLED`；`CLOSED` 和已取消任务不能再次转移

### Requirement: Transactional state persistence and events

系统 SHALL 在同一写事务中锁定任务、校验并更新 `row_version`、追加状态事件；任一步失败不得留下状态或事件半成品。

#### Scenario: State update uses optimistic version

- **WHEN** 持久化前任务行版本已变化
- **THEN** 状态服务失败并不追加状态事件

#### Scenario: State event uses the audit chain

- **WHEN** 合法状态转移成功
- **THEN** 追加 `TASK_STATE_CHANGED` 审计事件，包含必要的 from/to/reason 元数据和当前任务标识

#### Scenario: Harness and Controller cannot bypass the state machine

- **WHEN** 检查模块依赖和状态更新入口
- **THEN** 业务状态推进只存在于状态机应用服务，Controller/Harness 不直接调用数据库状态更新
