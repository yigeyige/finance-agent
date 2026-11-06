# frontend-identity-navigation Specification

## Purpose
TBD - created by archiving change test-identity-permission-navigation. Update Purpose after archive.

## Requirements

### Requirement: Permission-driven navigation

前端 SHALL 根据当前身份接口返回的权限显示导航，不得把导航可见性当作服务端授权；首次打开受保护深链时，路由守卫 MUST 等待当前身份加载完成后再判断权限。缺少登录态时，前端 MUST 导航至登录页，并在登录成功后返回原受保护路径；未携带 `redirect` 时 MUST 进入首页 `/home`。登录成功后从登录页切换到工作台时，根组件 MUST 同步最新身份状态。左侧导航 MUST 在所有已登录身份下显示“首页”，PLATFORM_USER 显示首页、业务处理、任务列表、业务场景、异常堆栈五个菜单；PLATFORM_ADMIN 显示全部菜单及用户管理。

#### Scenario: Platform user navigation

- **WHEN** PLATFORM_USER 登录并打开工作台
- **THEN** 左侧显示首页、业务处理、任务列表、业务场景、异常堆栈五个指定菜单，配置和用户管理深链被拒绝

#### Scenario: Platform administrator navigation

- **WHEN** PLATFORM_ADMIN 登录并打开工作台
- **THEN** 左侧显示全部菜单并可进入用户管理

#### Scenario: Login lands on home page

- **WHEN** 用户登录成功且未携带 `redirect` 查询参数
- **THEN** 前端进入首页 `/home`，而不是直接进入业务处理页

#### Scenario: Root path opens home page

- **WHEN** 用户访问根路径 `/`
- **THEN** 前端重定向到 `/home`；旧的 `draft`、`skillId` 查询参数不带到首页

#### Scenario: Refresh an authorized deep link

- **WHEN** 拥有目标权限的用户直接刷新任务诊断、证据或决策包路由
- **THEN** 前端先加载身份再放行原路由，不得因初始缓存为空而重定向首页

#### Scenario: Route allows authorized platform role

- **WHEN** 已发布决策包路由允许 `TASK_READ` 或 `PUBLISHED_PACKET_READ` 中的任一权限
- **THEN** 平台用户与管理员均能进入受保护页面，页面内写操作仍由后端授权

#### Scenario: Unauthenticated user opens a protected route

- **WHEN** 未登录用户直接访问首页、业务处理、任务、业务场景或大模型配置路由
- **THEN** 前端跳转登录页，登录成功后返回原访问路径

#### Scenario: Logged-in user opens user menu

- **WHEN** 已登录用户点击页面右上角的当前用户显示
- **THEN** 前端展示包含退出登录操作的下拉菜单，且退出后清除本地身份缓存并跳转登录页

#### Scenario: First login loads navigation without refresh

- **WHEN** 用户在登录页完成验证并跳转工作台
- **THEN** 根组件同步最新身份，左侧立即显示该身份拥有权限的功能菜单，无需刷新页面

### Requirement: Viewport-persistent workbench sidebar
前端 SHALL 将工作台左侧导航渲染为独立的视口级区域；右侧页面内容高度变化不得缩短侧栏背景和菜单可用高度。侧栏菜单超过视口时 MUST 在侧栏内纵向滚动，且不得覆盖右侧工作区。

#### Scenario: Short management page keeps full sidebar
- **WHEN** 管理员打开内容不足一屏的任务列表、业务场景 Skill 或大模型配置页面
- **THEN** 左侧导航仍覆盖完整浏览器视口高度，所有已授权菜单保持可见或可在侧栏内滚动访问

#### Scenario: Long workbench page keeps navigation usable
- **WHEN** 管理员滚动一个内容超过一屏的工作台页面
- **THEN** 左侧导航保持在可视区域，菜单过长时仅在侧栏内滚动，不遮挡右侧内容

### Requirement: Platform configuration navigation exposes model configuration
前端 SHALL 仅向具有 `CONFIG_MANAGE` 权限的身份显示“大模型配置”菜单，并将其路由到受权限守卫保护的模型配置页面。

#### Scenario: Platform administrator opens model configuration
- **WHEN** 具有 `CONFIG_MANAGE` 权限的管理员打开左侧导航
- **THEN** 页面显示“大模型配置”，并可进入模型配置管理页面

#### Scenario: Non-config user opens model configuration route
- **WHEN** 不具有 `CONFIG_MANAGE` 权限的用户访问模型配置路由
- **THEN** 前端将其导回工作台，后端仍拒绝任何未授权配置请求

### Requirement: EMS business knowledge navigation

前端 SHALL 在左侧菜单隐藏“EMS业务知识域”入口；已存在的业务知识域路由 SHALL 继续按 `TASK_READ` 权限保护。左侧业务场景入口 SHALL 显示“业务场景技能”。

#### Scenario: Authorized user opens workbench navigation

- **WHEN** 具有 `TASK_READ` 权限的用户打开工作台左侧导航
- **THEN** 页面显示“业务场景技能”，不显示“EMS业务知识域”菜单

#### Scenario: Authorized user opens knowledge deep link

- **WHEN** 具有 `TASK_READ` 权限的用户访问已有 EMS业务知识域深链
- **THEN** 前端仍允许访问原页面

#### Scenario: Unauthorized user opens knowledge deep link

- **WHEN** 不具有 `TASK_READ` 权限的用户访问 EMS业务知识域路由
- **THEN** 前端将其导回工作台，且后端仍拒绝目录接口请求
