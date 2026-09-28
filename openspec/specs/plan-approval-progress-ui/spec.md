# plan-approval-progress-ui Specification

## Purpose
TBD - created by archiving change plan-approval-progress-ui. Update Purpose after archive.
## Requirements
### Requirement: Plan and approval card
工作台 SHALL 展示一个推荐步骤和最多两个备选，并显示来源绑定、范围修订、字段策略、查询模板、调用上限和有效期。

#### Scenario: Approve or reject a step
- **WHEN** OPERATOR 选择步骤并提交批准或拒绝
- **THEN** 请求携带当前 row version 和幂等键，页面显示审批结果

### Requirement: Public resumable progress
工作台 SHALL 仅展示当前任务的公开事件，并在连接断开时保留游标状态。

#### Scenario: Cross-task event
- **WHEN** SSE 收到其他任务的事件
- **THEN** 页面丢弃该事件，不更新当前任务进度

