## MODIFIED Requirements

### Requirement: Task intake and scope confirmation form
工作台 SHALL 提供任务标题、问题、业务对象、前提来源和范围确认表单，并在每次写请求进行中阻止重复提交。范围确认 SHALL 使用日期、调查内容和数据使用范围等业务字段，由系统生成后端所需的结构化对象和字段数组，不得要求业务人员输入 JSON。任务草稿创建成功后 SHALL 进入业务对象步骤；前提保存成功后 SHALL 进入范围确认步骤；范围确认成功后 SHALL 显示业务状态和下一步操作。

#### Scenario: Create and confirm scope
- **WHEN** OPERATOR 填写任务、业务对象和调查范围并依次提交
- **THEN** 页面按顺序推进，系统自动生成范围请求并显示可理解的完成状态

#### Scenario: Task draft creation fails
- **WHEN** 任务草稿创建请求发生网络错误或后端拒绝请求
- **THEN** 页面保留任务问题表单、恢复提交按钮并显示明确中文错误提示

#### Scenario: Duplicate task draft submission is blocked
- **WHEN** 任务草稿创建请求仍在处理中且用户再次触发提交
- **THEN** 页面不发送第二个创建请求

#### Scenario: Incomplete business scope
- **WHEN** 业务对象、日期范围或调查内容未填写完整
- **THEN** 页面阻止提交并用业务语言指出缺失内容，不显示 JSON 格式错误
