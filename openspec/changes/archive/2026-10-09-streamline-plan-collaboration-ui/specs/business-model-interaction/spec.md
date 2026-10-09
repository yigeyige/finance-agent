## ADDED Requirements

### Requirement: Safe model metadata appears in technical details
前端 SHALL 在当前业务处理的技术明细中展示模型显示名称、运行模式、Token 数和调用耗时。前端 MUST 仅在受控响应提供非空推理强度时展示该项，且 MUST NOT 展示提示词、推理过程、输入摘要或输出摘要原文。

#### Scenario: Guidance metadata is available
- **WHEN** 页面已收到业务处理初稿或会话修订的模型元数据
- **THEN** 技术明细展示允许的模型信息，并保留真实模型与本地模拟模型的明确区分

#### Scenario: Reasoning effort is absent
- **WHEN** 受控模型响应未提供推理强度
- **THEN** 页面不显示推理强度字段，且不把缺失值替换为推理内容或错误提示
