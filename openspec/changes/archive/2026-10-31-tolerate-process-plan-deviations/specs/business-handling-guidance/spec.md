## MODIFIED Requirements

### Requirement: Controlled handling draft and chat revision
系统 SHALL 在业务标题和业务说明创建任务后，提供受控模型交互以生成或修订业务处理草稿。草稿 MUST 包含处理对象、推荐方案、简短说明、检查结论和关联证据；模型输入 MUST 仅来自服务端任务投影、管理员明确提交的消息及白名单化草稿字段。初稿请求可以包含后端校验的受限文本附件投影；系统 MUST NOT 接受浏览器直接提交的任意附件正文、Skill 内容或其他模型上下文。

对于「审批流程生成-隆地」场景，若模型返回的 processPlan 存在可安全规范化的偏差（显式 start/end 节点、kind 别名、assignee 为 null、节点 ID 含非法字符、无效条件或引用），系统 SHALL 规范化后返回草稿并附带 warnings；无法安全修复时 SHALL 置空 processPlan 并返回其余草稿与 warnings，MUST NOT 因 processPlan 结构偏差丢弃整份草稿或返回模型不可用。非流程场景 MUST NOT 返回 processPlan。reply、draft、requestedAttachments 和草稿必填字段仍 MUST 严格校验。

#### Scenario: Initial handling draft is generated
- **WHEN** OPERATOR 已创建有效业务标题和业务说明的任务并请求处理初稿
- **THEN** 系统返回严格校验的对象范围初稿、推荐业务方案、模型显示信息和非敏感调用元数据

#### Scenario: Initial draft includes conclusion and evidence
- **WHEN** 存在启用的 EMS 连接且模型在生成初稿前取得受控查询证据
- **THEN** 系统返回的初稿包含检查结论与结构化关联证据，且证据不包含连接凭据或未脱敏的内部路径

#### Scenario: Administrator revises the draft in chat
- **WHEN** OPERATOR 提交不超过约定长度的中文修改意见和当前草稿
- **THEN** 系统返回可显示的中文回复与完整的新草稿，且不保存推理过程、原始提示词或聊天记录

#### Scenario: Unsupported draft value is rejected
- **WHEN** 聊天请求包含未允许的处理内容、无效日期或超出长度限制的字段
- **THEN** 系统拒绝请求并返回业务可理解的错误，且不调用模型

#### Scenario: Initial draft is requested with attachments
- **WHEN** OPERATOR 在新建业务处理页面选择合法附件并开始模型分析
- **THEN** 页面提交受控 multipart 初稿请求，保留附件选择状态直到请求完成，并允许在提交前移除附件

#### Scenario: Process plan deviations are normalized
- **WHEN** 流程场景模型返回的 processPlan 含显式 start/end、kind 别名、assignee 为 null、非法节点 ID 或无效条件
- **THEN** 系统在初稿生成阶段规范化这些结构并返回草稿，同时在 warnings 中列出规范化项

#### Scenario: Unrecoverable process plan is returned with warning
- **WHEN** 流程场景模型返回的 processPlan 无法安全修复为合法结构
- **THEN** 系统返回其余草稿、空的 processPlan 和警告，不返回模型不可用错误，并允许用户通过对话补充说明后重新分析

#### Scenario: Guidance response exposes process plan and warnings
- **WHEN** OPERATOR 请求流程场景初稿
- **THEN** 响应包含可供页面展示的 processPlan（或空值）与 warnings，便于用户在生成决策包前检查

#### Scenario: Non-process scene omits process plan
- **WHEN** OPERATOR 对未关联审批流程场景的任务请求初稿
- **THEN** 响应不包含 processPlan，其余草稿字段保持既有严格校验
