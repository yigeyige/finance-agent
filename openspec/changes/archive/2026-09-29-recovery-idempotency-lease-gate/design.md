## Context

恢复行为必须先区分可安全重放、需要新审批和需要人工对账，租约必须保证同一任务只有一个有效 owner，幂等键必须防止重复调用和重复 revision。已有单元测试是行为依据，本变更只把它们组织成可执行的 QA 矩阵。

## Decisions

1. fixture 只包含 synthetic task/step/worker 标识、故障点、输入状态、预期 `CheckpointRecoveryDecision` 和副作用计数。
2. 脚本默认只校验矩阵；`-Execute` 只读取用户保存的故障注入结果 JSON，不连接应用或数据库。
3. SQL 模板只返回按 task 聚合的 lease、tool invocation、outbox、packet revision 和 checkpoint 计数，不返回 payload。
4. 目标断言固定为：双执行 0、重复 revision 0、跳过审批 0、UNKNOWN 调用进入 `MANUAL_RECONCILIATION`；其他不确定状态不得自动重放。
