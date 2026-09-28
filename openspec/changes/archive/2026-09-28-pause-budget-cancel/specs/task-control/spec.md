## MODIFIED Requirements

### Requirement: Bounded budget control

系统 SHALL 在模型调用前按步骤数、token 数和成本分别校验任务预算；任一预算达到上限时拒绝本次消费并返回 `BUDGET_EXHAUSTED`，不得继续调用模型。

#### Scenario: Budget exhaustion is rejected

- **WHEN** 任一预算消费后超过配置上限
- **THEN** 预算计数器返回拒绝结果，调用方可将任务置为 `PAUSED_BUDGET`

### Requirement: Pause and cancellation control

系统 SHALL 提供统一任务控制服务，支持模型不可用、来源不可用、预算耗尽、可恢复失败暂停以及用户取消；取消 SHALL 释放当前活动租约且不删除历史。

#### Scenario: Paused task is not claimed

- **WHEN** 任务进入任一暂停状态
- **THEN** Worker 领取查询不再返回该任务的步骤，已存证据仍可读取

#### Scenario: Cancellation releases lease and preserves history

- **WHEN** 用户取消仍有活动租约的任务
- **THEN** 任务进入 `CANCELLED`、租约被释放，历史状态事件和工具调用保留
