## ADDED Requirements

### Requirement: Viewport-persistent workbench sidebar
前端 SHALL 将工作台左侧导航渲染为独立的视口级区域；右侧页面内容高度变化不得缩短侧栏背景和菜单可用高度。侧栏菜单超过视口时 MUST 在侧栏内纵向滚动，且不得覆盖右侧工作区。

#### Scenario: Short management page keeps full sidebar
- **WHEN** 管理员打开内容不足一屏的任务列表、业务场景 Skill 或大模型配置页面
- **THEN** 左侧导航仍覆盖完整浏览器视口高度，所有已授权菜单保持可见或可在侧栏内滚动访问

#### Scenario: Long workbench page keeps navigation usable
- **WHEN** 管理员滚动一个内容超过一屏的工作台页面
- **THEN** 左侧导航保持在可视区域，菜单过长时仅在侧栏内滚动，不遮挡右侧内容
