# task-attachment-archive Specification

## Purpose
TBD - created by archiving change add-task-attachment-archive. Update Purpose after archive.
## Requirements
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

