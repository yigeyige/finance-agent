## MODIFIED Requirements

### Requirement: Controlled handling draft and chat revision
系统 SHALL 在业务标题和业务说明创建任务后，提供受控模型交互以生成或修订业务处理草稿。草稿 MUST 包含处理类型、对象前提、信息来源、系统、日期范围、处理内容、推荐方案和简短说明；模型输入 MUST 仅来自服务端任务投影、管理员明确提交的消息及白名单化草稿字段。

#### Scenario: Initial handling draft is generated
- **WHEN** OPERATOR 已创建有效业务标题和业务说明的任务并请求处理初稿
- **THEN** 系统返回严格校验的对象范围初稿、推荐业务方案、模型显示信息和非敏感调用元数据

#### Scenario: Administrator revises the draft in chat
- **WHEN** OPERATOR 提交不超过约定长度的中文修改意见和当前草稿
- **THEN** 系统返回可显示的中文回复与完整的新草稿，且不保存推理过程、原始提示词或聊天记录

#### Scenario: Unsupported draft value is rejected
- **WHEN** 聊天请求包含未允许的处理内容、无效日期或超出长度限制的字段
- **THEN** 系统拒绝请求并返回业务可理解的错误，且不调用模型

### Requirement: Business scenario Skill management
系统 SHALL 为 OPERATOR 提供业务场景 Skill 的列表、新增、编辑和删除能力。每个 Skill MUST 包含业务名称、业务说明、处理方式和业务场景；接口 MUST 校验输入字段，删除 MUST 仅对当前有效记录生效并保留操作审计。

#### Scenario: Administrator creates or edits a business scenario Skill
- **WHEN** OPERATOR 提交合法的业务名称、业务说明、处理方式和业务场景
- **THEN** 系统保存 Skill 并返回最新内容，后续列表显示该 Skill

#### Scenario: Administrator removes a business scenario Skill
- **WHEN** OPERATOR 确认删除一个当前有效的 Skill
- **THEN** 系统将其标记为删除、记录操作者和时间，且后续列表不再显示该 Skill

#### Scenario: Non-operator attempts to maintain Skills
- **WHEN** 非 OPERATOR 身份请求新增、编辑或删除业务场景 Skill
- **THEN** 系统拒绝写操作且不修改任何 Skill
