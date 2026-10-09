# frontend-identity-navigation Specification

## Purpose
TBD - created by archiving change test-identity-permission-navigation. Update Purpose after archive.
## Requirements
### Requirement: Permission-driven navigation
前端 SHALL 根据当前身份接口返回的权限显示导航，不得把导航可见性当作服务端授权；首次打开受保护深链时，路由守卫 MUST 等待当前身份加载完成后再判断权限。

#### Scenario: Viewer navigation
- **WHEN** 当前身份为 VIEWER
- **THEN** 前端只显示公开查看入口，不显示任务写入入口

#### Scenario: Refresh an authorized deep link
- **WHEN** 拥有目标权限的操作员直接刷新任务诊断、证据或决策包路由
- **THEN** 前端先加载身份再放行原路由，不得因初始缓存为空而重定向首页

#### Scenario: Route allows either authorized role
- **WHEN** 已发布决策包路由允许 `TASK_READ` 或 `PUBLISHED_PACKET_READ` 中的任一权限
- **THEN** 操作员和查看员均能进入受保护页面，页面内写操作仍按角色隐藏并由后端授权

### Requirement: Local test identity switcher
身份切换入口 SHALL 仅在 local/test 配置显示，并通过 API 客户端传递受控测试身份标识。

#### Scenario: Production hides switcher
- **WHEN** 前端运行在生产配置
- **THEN** 页面不显示测试身份切换入口

### Requirement: Platform configuration navigation exposes model configuration
前端 SHALL 仅向具有 `CONFIG_MANAGE` 权限的身份显示“大模型配置”菜单，并将其路由到受权限守卫保护的模型配置页面。

#### Scenario: Platform administrator opens model configuration
- **WHEN** 具有 `CONFIG_MANAGE` 权限的管理员打开左侧导航
- **THEN** 页面显示“大模型配置”，并可进入模型配置管理页面

#### Scenario: Non-config user opens model configuration route
- **WHEN** 不具有 `CONFIG_MANAGE` 权限的用户访问模型配置路由
- **THEN** 前端将其导回工作台，后端仍拒绝任何未授权配置请求
