## ADDED Requirements

### Requirement: Model configuration dialog consistency
系统 SHALL 为大模型配置的新增和编辑弹窗提供与工作台一致的暖白圆角容器、清晰标题区、可读表单间距和底部操作区；窄屏下表单和按钮 MUST 保持可操作。API Key 的写入型处理、字段校验、启停与保存行为 MUST 保持不变。

#### Scenario: Administrator opens a model configuration dialog
- **WHEN** PLATFORM_ADMIN 打开新增或编辑大模型配置弹窗
- **THEN** 页面以统一工作台弹窗样式展示已有字段和保存、取消操作，且不回显 API Key
