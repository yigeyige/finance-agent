## ADDED Requirements

### Requirement: Unified model configuration entry layout
系统 SHALL 在大模型配置管理页面使用与其他管理入口一致的紧凑概览卡片、操作区和圆角记录卡片，并展示模型配置数量及启用状态等非敏感信息。

#### Scenario: Administrator manages model configurations
- **WHEN** PLATFORM_ADMIN 进入大模型配置页面
- **THEN** 系统以统一入口布局展示模型配置记录，并保留新增、编辑、启用和停用操作及既有密钥保护行为
