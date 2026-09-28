# harness-session-plan Specification

## Purpose
TBD - created by archiving change harness-session-plan-mapping. Update Purpose after archive.
## Requirements
### Requirement: Session binding boundary

系统 SHALL 将 Harness session 显式绑定到 task、user、runtime bundle 和确认的 scope revision，并按完整 session/task/user 边界读写状态。

#### Scenario: Bound session is created
- **WHEN** 提交完整身份和运行版本/范围绑定
- **THEN** 创建 session 状态，后续计划只能在同一 task、user、session 边界内追加

#### Scenario: Cross-task session is rejected
- **WHEN** 使用其他 task 或 user 读取/追加 session 或 plan
- **THEN** 请求失败，不返回或修改其他业务任务状态

### Requirement: Plan mapping requires business approval

系统 SHALL 将 Harness plan revision 映射为一个推荐和最多两个备选的业务步骤提议，不自动执行、不预取数据、不直接推进任务状态。

#### Scenario: Valid plan produces proposal command
- **WHEN** 当前绑定 session 提交包含 1-3 个逻辑步骤且恰好一个推荐项的 plan
- **THEN** 返回业务 `ProposeStepsCommand`，保留 task 边界并等待后续审批

#### Scenario: Invalid plan is rejected
- **WHEN** plan 包含额外字段、超过两个备选、缺少推荐项或越过 session/task 边界
- **THEN** 映射失败，不产生步骤执行或数据读取

