# plan-approval-progress-ui Specification

## Purpose
TBD - created by archiving change plan-approval-progress-ui. Update Purpose after archive.
## Requirements
### Requirement: Plan and approval card
工作台 SHALL 在管理员确认对象和范围后，把模型协作形成的一条推荐业务方案提交为步骤提议，并用业务语言说明方案将处理的内容。对象与范围确认区 MUST 位于上方，推荐方案编辑与模型聊天 MUST 在下方同一协作区域左右并列展示，并在窄屏自动纵向堆叠，呈现确认范围、生成初步方案、模型修改和保存修改的顺序；页面 MUST 自动生成目标摘要、来源绑定、范围修订和内部策略版本，不得要求管理员填写 UUID、哈希、版本字符串、批准决定或调用次数。

#### Scenario: Generate a business plan
- **WHEN** OPERATOR 已确认对象和范围并编辑推荐方案后点击生成业务方案
- **THEN** 系统提交一条推荐步骤，页面展示方案已生成及其处理摘要，而不显示备选方案、批准/拒绝控件

#### Scenario: Revise a generated business plan with the model
- **WHEN** OPERATOR 在业务方案已生成且尚未执行时，通过同一协作区域提交模型修改意见
- **THEN** 页面展示模型回复和更新后的推荐方案，并提供保存修改后的方案入口

#### Scenario: Generate model business plan
- **WHEN** 业务标题和说明创建任务后，OPERATOR 进入模型协作步骤
- **THEN** 页面展示模型分析状态、模型初稿、会话修改入口和可编辑的推荐方案，管理员无需从空白技术表单开始

#### Scenario: Model plan generation fails
- **WHEN** 模型方案调用失败、超时或输出不符合结构
- **THEN** 页面保留已确认范围，显示中文失败说明和重试入口，不提交步骤提议

### Requirement: Public resumable progress
工作台 SHALL 仅展示当前任务的公开事件，并在连接断开时保留游标状态。

#### Scenario: Cross-task event
- **WHEN** SSE 收到其他任务的事件
- **THEN** 页面丢弃该事件，不更新当前任务进度
