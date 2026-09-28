## Context

任务状态机已定义暂停与取消状态，Worker 领取 SQL 只领取 `WAITING_STEP_APPROVAL` 任务；当前缺少预算使用判定和统一控制服务。

## Decisions

1. 使用纯 Java `BudgetCounter` 计算步骤、token 和成本预算，超限只返回拒绝结果，不继续消耗模型调用。
2. `TaskControlService` 复用现有状态推进服务和租约服务；暂停只推进任务状态，取消在同一事务中推进 `CANCELLED` 并释放活动租约，不删除任何历史记录。
3. 暂停原因通过明确错误码和状态事件记录，后续恢复仍由既有状态机及审批守卫控制。

## Non-Goals

- 不启动本地服务，不自动部署。
- 不引入新的框架或外部依赖。

