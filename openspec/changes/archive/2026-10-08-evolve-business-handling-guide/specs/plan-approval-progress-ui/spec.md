## MODIFIED Requirements

### Requirement: Plan and approval card
工作台 SHALL 在管理员确认对象和范围后，把模型协作形成的推荐业务方案和最多两个备选方案直接提交为步骤提议，并用业务语言说明方案将处理的内容。页面 MUST 自动生成目标摘要、来源绑定、范围修订和内部策略版本，不得要求管理员填写 UUID、哈希、版本字符串、批准决定或调用次数。

#### Scenario: Generate a business plan
- **WHEN** OPERATOR 已确认对象和范围并选择或编辑模型方案后点击生成业务方案
- **THEN** 系统提交推荐步骤和备选步骤，页面展示方案已生成及其处理摘要，而不显示批准/拒绝控件

#### Scenario: Generate model business plan
- **WHEN** 业务标题和说明创建任务后，OPERATOR 进入模型协作步骤
- **THEN** 页面展示模型分析状态、模型初稿、会话修改入口和可编辑的推荐方案，管理员无需从空白技术表单开始

#### Scenario: Model plan generation fails
- **WHEN** 模型方案调用失败、超时或输出不符合结构
- **THEN** 页面保留已填写的业务说明和对象范围，显示中文失败说明和重试入口，不提交步骤提议

### Requirement: Public resumable progress
工作台 SHALL 仅展示当前任务的公开事件，并在连接断开时保留游标状态。

#### Scenario: Cross-task event
- **WHEN** SSE 收到其他任务的事件
- **THEN** 页面丢弃该事件，不更新当前任务进度
