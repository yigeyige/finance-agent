## Why

当前管理工作台的菜单术语、列表信息密度和表格滚动不一致，任务列表也缺少管理员识别任务所需的业务上下文。

## What Changes

- 隐藏 EMS 业务知识域菜单入口，并将业务场景 Skill 菜单显示为业务场景技能。
- 将数据库实例只读提醒移到标题右侧，使宽屏表格利用可用宽度。
- 精简任务列表标题区域，修正分页和滚动布局。
- 扩展任务列表只读响应，展示发起人名称、业务说明、业务场景和数据库实例。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `frontend-identity-navigation`：调整菜单入口和中文名称。
- `ems-database-connection-analysis`：调整实例页面提醒和表格响应式布局。
- `task-list-audit-query`：扩展列表展示字段与分页、滚动行为。

## Impact

涉及前端菜单、数据库实例页、任务列表页，任务列表 API 响应、前端解析契约及对应规格和测试；无新依赖、数据库迁移或运行时写操作。
