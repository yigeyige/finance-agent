## MODIFIED Requirements

### Requirement: Permission-driven navigation
前端 SHALL 根据当前身份接口返回的权限显示导航，不得把导航可见性当作服务端授权；首次打开受保护深链时，路由守卫 MUST 等待当前身份加载完成后再判断权限。缺少登录态时，前端 MUST 导航至登录页，并在登录成功后返回原受保护路径。

#### Scenario: Unauthenticated user opens a protected route
- **WHEN** 未登录用户直接访问任务、业务场景 Skill 或大模型配置路由
- **THEN** 前端跳转登录页，登录成功后返回原访问路径

#### Scenario: Logged-in user opens user menu
- **WHEN** 已登录用户点击页面右上角的当前用户显示
- **THEN** 前端展示包含退出登录操作的下拉菜单，且退出后清除本地身份缓存并跳转登录页

## REMOVED Requirements

### Requirement: Local test identity switcher
