## MODIFIED Requirements

### Requirement: Unified model configuration entry layout
系统 SHALL 在大模型配置管理页面使用与其他管理入口一致的标题、操作区和圆角记录卡片。页面 MUST 在标题后直接展示配置操作和记录内容，不得展示重复的配置数量或启用状态总览卡片；非敏感记录信息仍在表格中展示。

#### Scenario: Administrator manages model configurations
- **WHEN** PLATFORM_ADMIN 进入大模型配置页面
- **THEN** 系统以统一入口布局展示模型配置记录，并保留新增、编辑、启用和停用操作及既有密钥保护行为
