## ADDED Requirements

### Requirement: Model guidance is visible and editable
前端 SHALL 在业务处理向导中展示模型的初稿、模式、显示名称、简短理由和会话回复。管理员 MUST 能编辑所有业务字段，并能通过自然语言修改意见请求新的结构化初稿。

#### Scenario: Stub guidance is clearly identified
- **WHEN** 当前环境启用模型 Stub 并返回业务处理初稿或会话修订
- **THEN** 页面明确显示“本地模拟模型”，不将结果表述为真实模型输出

#### Scenario: Model request fails
- **WHEN** 初稿或会话修订请求失败、超时或响应不符合结构
- **THEN** 页面保留管理员已填写内容，展示中文失败说明和重试入口，不提交前提、范围或业务方案
