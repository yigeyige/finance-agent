## ADDED Requirements

### Requirement: Task attachment archive

系统 SHALL 在首次带附件模型分析前，把用户上传的附件写入任务附件库。每条记录 MUST 包含任务标识、原始文件字节、文本投影、SHA-256、大小、内容类型、截断标记和上传时间。同一任务内相同 SHA-256 MUST 去重。落库失败时系统 MUST 中止本次模型分析并返回明确错误，MUST NOT 在无留痕的情况下调用模型。

#### Scenario: Attachment is archived before analysis
- **WHEN** 用户带附件请求首次模型分析
- **THEN** 系统先写入任务附件库，再调用模型

#### Scenario: Archive fails
- **WHEN** 附件落库失败
- **THEN** 系统返回明确错误，不调用模型

#### Scenario: Duplicate attachment
- **WHEN** 同一任务重复上传相同内容的文件
- **THEN** 不产生重复记录

#### Scenario: Text projection matches model input
- **WHEN** 附件文本被截断后传给模型
- **THEN** 任务附件库保存相同的截断文本和 truncated 标记

## ADDED Requirements

### Requirement: Task attachment list and download

系统 SHALL 提供按任务查询附件元数据的接口，以及按附件下载原始文件的接口。列表 MUST NOT 返回附件正文；下载 MUST 校验附件属于该任务。

#### Scenario: List attachments
- **WHEN** 用户查询任务附件
- **THEN** 返回文件名、类型、大小、SHA-256、截断标记和上传时间

#### Scenario: Download attachment
- **WHEN** 用户下载属于该任务的附件
- **THEN** 返回原始文件字节和正确的文件名

#### Scenario: Cross-task download
- **WHEN** 请求的附件不属于该任务
- **THEN** 系统拒绝下载

## ADDED Requirements

### Requirement: Full attachment text injection

上传附件的文本投影 SHALL 全文注入模型，MUST NOT 在本地静默截断；超过模型输入预算时 MUST 明确失败并提示输入过大。

#### Scenario: Full text is injected
- **WHEN** 附件文本成功提取
- **THEN** 模型输入包含完整文本，truncated 为 false

#### Scenario: Input budget exceeded
- **WHEN** 全文注入超过模型输入预算
- **THEN** 系统明确失败，不静默截断

### Requirement: BPMN XML BOM cleanup

BPMN XML 解析前 SHALL 去除 BOM、前导空白及首个标签前的非 XML 内容，避免“前言中不允许有内容”解析错误。

#### Scenario: BOM prefixed XML
- **WHEN** 样例 XML 以 BOM 或前导空白开头
- **THEN** 系统清理后再解析，结构投影成功
