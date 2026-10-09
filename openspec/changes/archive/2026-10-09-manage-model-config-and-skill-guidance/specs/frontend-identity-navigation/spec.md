## ADDED Requirements

### Requirement: Platform configuration navigation exposes model configuration
前端 SHALL 仅向具有 `CONFIG_MANAGE` 权限的身份显示“大模型配置”菜单，并将其路由到受权限守卫保护的模型配置页面。

#### Scenario: Platform administrator opens model configuration
- **WHEN** 具有 `CONFIG_MANAGE` 权限的管理员打开左侧导航
- **THEN** 页面显示“大模型配置”，并可进入模型配置管理页面

#### Scenario: Non-config user opens model configuration route
- **WHEN** 不具有 `CONFIG_MANAGE` 权限的用户访问模型配置路由
- **THEN** 前端将其导回工作台，后端仍拒绝任何未授权配置请求
