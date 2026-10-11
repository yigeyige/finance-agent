# business-handling-guidance Specification

## Purpose
规定业务处理向导中受控模型初稿、会话修订、严格字段校验和人工确认的边界，使管理员能先获得可编辑建议，再安全确认对象、前提、范围和后续方案。

## Requirements

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

### Requirement: Handling draft does not bypass authoritative confirmation
模型草稿 SHALL 只用于协助管理员填写对象、前提和范围；系统 MUST 在管理员显式确认后才追加前提、创建范围修订或步骤提议。

#### Scenario: Draft is not persisted as scope
- **WHEN** 模型返回业务处理初稿
- **THEN** 任务仍保持未确认范围状态，直到 OPERATOR 提交有效的前提与范围确认请求

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

### Requirement: Task records selected business scenario Skill
系统 SHALL 允许业务处理任务在创建时关联一个当前有效的业务场景 Skill，并在后续业务处理初稿和会话修订中复用该关联。未选择 Skill 的任务 MUST 保持既有通用业务处理向导能力。

#### Scenario: Administrator starts handling from a business scenario Skill
- **WHEN** OPERATOR 从业务场景 Skill 目录进入新建业务处理并创建任务
- **THEN** 系统将所选 Skill 标识持久化到任务，并在后续模型初稿调用中使用该 Skill 的受控上下文

#### Scenario: Administrator creates a generic task
- **WHEN** OPERATOR 未选择业务场景 Skill 创建任务
- **THEN** 系统创建不关联 Skill 的任务，且业务处理向导仍可生成通用初稿

### Requirement: Unified administrative entry layout
系统 SHALL 为业务处理向导和业务场景 Skill 管理页面提供一致的工作台布局，包括统一的标题节奏、明确操作区和圆角内容卡片。业务场景 Skill 管理页面 MUST 在标题后直接展示操作区和记录内容，不得展示重复业务名称、状态或数量的总览卡片。

#### Scenario: Administrator switches between handling and Skill management
- **WHEN** 管理员在新建业务处理与业务场景 Skill 页面之间切换
- **THEN** 两个页面以一致的标题、操作和内容区层级展示，且原有任务创建、Skill 新增、编辑、删除和开始处理入口保持可用
