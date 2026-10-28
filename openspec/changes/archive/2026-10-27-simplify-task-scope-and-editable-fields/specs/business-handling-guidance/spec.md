# Spec Delta

## MODIFIED Requirements

### Requirement: Controlled handling draft and chat revision

系统 SHALL 在业务标题和业务说明创建任务后，提供受控模型交互以生成或修订业务处理草稿。草稿 MUST 包含处理对象、推荐方案、简短说明、检查结论和关联证据；模型输入 MUST 仅来自服务端任务投影、管理员明确提交的消息及白名单化草稿字段。初稿请求可以包含后端校验的受限文本附件投影；系统 MUST NOT 接受浏览器直接提交的任意附件正文、Skill 内容或其他模型上下文。

#### Scenario: Initial handling draft is generated
- **WHEN** OPERATOR 已创建有效业务标题和业务说明的任务并请求处理初稿
- **THEN** 系统返回严格校验的处理对象初稿、推荐业务方案、模型显示信息和非敏感调用元数据

#### Scenario: Initial draft includes conclusion and evidence
- **WHEN** 任务选择了启用的 EMS 实例且模型在生成初稿前取得受控查询证据
- **THEN** 系统返回的初稿包含检查结论与结构化关联证据，且证据不包含连接凭据或未脱敏的内部路径

#### Scenario: Administrator revises the draft in chat
- **WHEN** OPERATOR 提交不超过约定长度的中文修改意见和当前草稿
- **THEN** 系统返回可显示的中文回复与完整的新草稿，且不保存推理过程、原始提示词或聊天记录

#### Scenario: Unsupported draft value is rejected
- **WHEN** 聊天请求包含未允许的处理对象或超出长度限制的字段
- **THEN** 系统拒绝请求并返回业务可理解的错误，且不调用模型

#### Scenario: Initial draft is requested with attachments
- **WHEN** OPERATOR 在新建业务处理页面选择合法附件并开始模型分析
- **THEN** 页面提交受控 multipart 初稿请求，保留附件选择状态直到请求完成，并允许在提交前移除附件
