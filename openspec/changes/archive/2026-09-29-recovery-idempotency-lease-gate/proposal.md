## Why

恢复、租约和幂等逻辑已经分布在 Worker、checkpoint、调用意图和 outbox 组件中，但缺少一份故障注入顺序、恢复决策矩阵和结果查询模板。用户无法据此复核进程中断、双 Worker、未知结果和重复 HTTP 是否真的没有双执行或重复发布。

## What Changes

- 新增版本化故障注入 fixture，固定审批后、意图后、结果后、计算后、草稿后和发布事务中断点，以及租约/恢复预期。
- 新增默认离线的 PowerShell 矩阵校验脚本和 PostgreSQL 只读查询模板。
- 新增后端契约测试，验证 fixture 与现有恢复决策、租约边界、幂等 outbox 和 Worker 生命周期测试对齐。

## Capabilities

### New Capabilities

- `recovery-idempotency-lease-gate`: 提供恢复、重放、幂等和租约故障注入验收资产。

### Modified Capabilities

- 无

## Impact

- 新增 `scripts/fixtures/`、PowerShell/SQL 验收资产和后端契约测试。
- 不新增运行时依赖，不自动启动 Worker、不自动中断进程；人工注入由用户执行。
