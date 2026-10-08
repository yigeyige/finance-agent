## ADDED Requirements

### Requirement: Controlled handling draft and chat revision
系统 SHALL 在业务标题和业务说明创建任务后，提供受控模型交互以生成或修订业务处理草稿。草稿 MUST 包含处理类型、对象前提、信息来源、系统、日期范围、处理内容、推荐方案、备选方案和简短说明；模型输入 MUST 仅来自服务端任务投影、管理员明确提交的消息及白名单化草稿字段。

#### Scenario: Initial handling draft is generated
- **WHEN** OPERATOR 已创建有效业务标题和业务说明的任务并请求处理初稿
- **THEN** 系统返回严格校验的对象范围初稿、推荐业务方案、备选方案、模型显示信息和非敏感调用元数据

#### Scenario: Administrator revises the draft in chat
- **WHEN** OPERATOR 提交不超过约定长度的中文修改意见和当前草稿
- **THEN** 系统返回可显示的中文回复与完整的新草稿，且不保存推理过程、原始提示词或聊天记录

#### Scenario: Unsupported draft value is rejected
- **WHEN** 聊天请求包含未允许的处理内容、无效日期或超出长度限制的字段
- **THEN** 系统拒绝请求并返回业务可理解的错误，且不调用模型

### Requirement: Handling draft does not bypass authoritative confirmation
模型草稿 SHALL 只用于协助管理员填写对象、前提和范围；系统 MUST 在管理员显式确认后才追加前提、创建范围修订或步骤提议。

#### Scenario: Draft is not persisted as scope
- **WHEN** 模型返回业务处理初稿
- **THEN** 任务仍保持未确认范围状态，直到 OPERATOR 提交有效的前提与范围确认请求
