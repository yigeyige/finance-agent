## ADDED Requirements

### Requirement: Generated recommendation can be revised before execution
系统 SHALL 允许管理员仅在当前推荐步骤尚未执行、任务仍处于待处理状态时更新已生成业务方案的推荐目标。更新请求 MUST 只接受受限的目标文本，系统 MUST 校验任务、当前步骤和步骤状态，并记录可审计事件。

#### Scenario: Revise an unexecuted recommendation
- **WHEN** OPERATOR 提交当前任务的推荐步骤标识和有效的新推荐方案，且任务处于 `WAITING_STEP_APPROVAL`、步骤处于 `PROPOSED`
- **THEN** 系统更新该推荐步骤目标、保留范围绑定并返回最新步骤视图

#### Scenario: Reject an executed or non-current recommendation
- **WHEN** OPERATOR 请求修改已批准、运行、完成或不属于当前任务推荐步骤的方案
- **THEN** 系统拒绝修改且不改变步骤、范围或任务状态
