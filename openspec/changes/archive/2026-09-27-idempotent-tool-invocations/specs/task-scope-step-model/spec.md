## MODIFIED Requirements

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
