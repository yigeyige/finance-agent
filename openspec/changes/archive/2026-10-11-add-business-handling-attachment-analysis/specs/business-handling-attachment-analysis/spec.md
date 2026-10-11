## ADDED Requirements

### Requirement: Restricted attachments support initial handling analysis
系统 SHALL 在新建业务处理的初稿请求中允许 OPERATOR 附带最多 3 个 `.txt`、`.md`、`.csv` 或 `.json` 文件。每个文件 MUST 非空、最大 1 MB 并使用有效 UTF-8 文本；系统 MUST 在后端将文件转换为有文件名和受限正文长度的临时文本投影，总正文不得超过 24,000 个字符。

#### Scenario: Operator submits supported attachments
- **WHEN** OPERATOR 在创建任务后选择不超过限制的受支持文本附件并请求模型初稿
- **THEN** 系统将受限附件投影与任务权威信息共同送入模型，并返回既有结构化初稿和非敏感调用元数据

#### Scenario: Attachment violates an input boundary
- **WHEN** OPERATOR 提交空文件、超过数量或大小限制、非 UTF-8 内容或不受支持的扩展名
- **THEN** 系统以中文业务错误拒绝该请求，不调用模型且不保存附件内容

### Requirement: Attachment content remains transient and non-authoritative
系统 MUST NOT 将原始附件、解析文本、文件正文摘要或模型提示词持久化到数据库、对象存储、日志或 API 响应。模型 MUST 被明确指示将附件视为待核验的用户提供材料；附件内容 SHALL NOT 直接创建或修改任务前提、范围修订、步骤或审批。

#### Scenario: Model returns a draft based on attachments
- **WHEN** 模型基于任务信息和受限附件文本返回业务处理草稿
- **THEN** 系统仅返回既有可编辑草稿，管理员仍须显式确认前提、范围和业务方案
