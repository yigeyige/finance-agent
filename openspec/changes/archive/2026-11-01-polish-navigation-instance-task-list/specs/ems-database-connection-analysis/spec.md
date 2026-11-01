## ADDED Requirements

### Requirement: Database instance page compact header and responsive table

数据库实例页面 SHALL 将现有只读安全提醒放在页面标题右侧同一栏。宽屏下配置表格 SHALL 使用可用宽度，避免无意义的横向滚动；窄屏下 SHALL 保留表格内横向滚动与可读列宽。

#### Scenario: Wide administrator viewport

- **WHEN** 管理员在可容纳全部列的宽屏打开数据库实例页面
- **THEN** 提醒位于标题右侧，表格不出现横向滚动条

#### Scenario: Narrow administrator viewport

- **WHEN** 管理员在窄屏打开数据库实例页面
- **THEN** 提醒可换行且表格列保持可读，通过表格区域横向滚动访问
