## Why

工作台侧栏会随右侧短页面缩短，导致导航未覆盖完整视口；管理入口的重复总览卡片也挤占了业务操作和数据展示空间。需要将导航稳定为独立的视口级区域，并以更直接的页面层级呈现管理功能。

## What Changes

- 将工作台左侧导航调整为独立的全视口高度区域，菜单过长时在侧栏内滚动。
- 移除任务列表、业务场景 Skill 和大模型配置页面的总览卡片，保留标题、操作区、表格和原有业务操作。
- 更新前端样式规范，明确管理页不再使用总览卡片。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `frontend-identity-navigation`: 左侧导航的布局高度与滚动行为调整。
- `business-handling-guidance`: 业务场景 Skill 管理页面的层级调整。
- `model-connection-management`: 大模型配置管理页面的层级调整。
- `task-list-audit-query`: 任务列表的页面层级调整。

## Impact

- 影响 `frontend/src/styles/index.css`、工作台三处管理页面组件和前端样式规范。
- 不修改后端接口、权限规则、数据库结构或现有表格操作。
