## MODIFIED Requirements

### Requirement: Task intake and scope confirmation form
工作台 SHALL 以业务说明、模型协作、决策包三步业务处理向导提供任务标题、业务说明、对象前提和范围确认表单，并在每次写请求进行中阻止重复提交。第一步 SHALL 提供业务标题、业务说明、业务场景 Skill 选择与可选数据库实例；创建任务后第二步 SHALL 展示可编辑的处理对象、业务说明、业务场景 Skill 与数据库实例。决策包生成成功后第三步 SHALL 独立展示成果，并可返回第二步继续协作；生成失败 SHALL 保留协作上下文。管理员通过“新建业务处理”入口明确开始新处理时，缓存的向导 MUST 清空上一份本地草稿并回到第一步；写请求或模型交互进行中时 MUST 保持当前草稿，避免旧异步结果覆盖新状态。

#### Scenario: Create and confirm handling scope
- **WHEN** OPERATOR 填写业务标题和说明，接受或修改模型初稿，并确认对象和处理范围
- **THEN** 页面按三步推进，系统先创建任务，第二步进行模型协作并保留任务信息编辑与证据展示，成功生成后第三步展示业务决策包

#### Scenario: Task draft creation fails

- **WHEN** 任务草稿创建请求发生网络错误或后端拒绝请求
- **THEN** 页面保留任务问题表单、恢复提交按钮并显示明确中文错误提示

#### Scenario: Duplicate task draft submission is blocked

- **WHEN** 任务草稿创建请求仍在处理中且用户再次触发提交
- **THEN** 页面不发送第二个创建请求

#### Scenario: Incomplete business scope
- **WHEN** 处理对象、日期范围或处理内容未填写完整
- **THEN** 页面阻止确认并用业务语言指出缺失内容，不显示 JSON 格式错误

#### Scenario: Administrator starts a new handling after an existing draft
- **WHEN** OPERATOR 在已有业务处理草稿或已生成方案后再次点击“新建业务处理”
- **THEN** 页面清空上一份本地任务、模型草稿、方案和聊天内容，展示第一步的默认业务标题与说明表单，且不删除已创建的后端任务
