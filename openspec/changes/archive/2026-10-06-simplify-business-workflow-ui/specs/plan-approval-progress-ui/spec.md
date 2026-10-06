## MODIFIED Requirements

### Requirement: Plan and approval card
工作台 SHALL 展示一个推荐步骤和最多两个备选，并用业务语言说明每个步骤将检查的内容与调用上限。页面 MUST 自动生成目标摘要、来源绑定、范围修订和内部策略版本，不得要求业务人员填写 UUID、哈希或版本字符串。

#### Scenario: Approve or reject a step
- **WHEN** OPERATOR 选择步骤、确认检查次数并提交批准或拒绝
- **THEN** 系统自动补齐技术绑定，请求携带当前 row version 和幂等键，页面显示审批结果

#### Scenario: Generate suggested investigation plan
- **WHEN** 范围确认完成且业务人员进入调查方案阶段
- **THEN** 页面根据任务范围预填推荐步骤，业务人员无需从空白技术表单开始
