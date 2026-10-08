# plan-approval-progress-ui Specification

## Purpose
TBD - created by archiving change plan-approval-progress-ui. Update Purpose after archive.
## Requirements
### Requirement: Plan and approval card
工作台 SHALL 在范围确认后调用后端受控模型生成一个推荐步骤和最多两个备选，并用业务语言展示模型理由、模型模式和每个步骤将检查的内容。页面 MUST 明确区分“模型生成建议”和“人工批准执行”，自动生成目标摘要、来源绑定、范围修订和内部策略版本，不得要求业务人员填写 UUID、哈希或版本字符串。

#### Scenario: Approve or reject a step
- **WHEN** OPERATOR 选择模型建议步骤、确认检查次数并提交批准或拒绝
- **THEN** 系统自动补齐技术绑定，请求携带当前 row version 和幂等键，页面显示人工审批结果

#### Scenario: Generate model investigation plan
- **WHEN** 范围确认完成且业务人员进入调查方案阶段
- **THEN** 页面显示模型分析状态，随后展示模型推荐步骤、备选步骤、生成理由和模型模式，业务人员无需从空白技术表单开始

#### Scenario: Model plan generation fails
- **WHEN** 模型方案调用失败、超时或输出不符合结构
- **THEN** 页面保留已确认范围，显示中文失败说明和重试入口，不提交步骤提议

### Requirement: Public resumable progress
工作台 SHALL 仅展示当前任务的公开事件，并在连接断开时保留游标状态。

#### Scenario: Cross-task event
- **WHEN** SSE 收到其他任务的事件
- **THEN** 页面丢弃该事件，不更新当前任务进度
