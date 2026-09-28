## ADDED Requirements

### Requirement: Permission-driven navigation
前端 SHALL 根据当前身份接口返回的权限显示导航，不得把导航可见性当作服务端授权。

#### Scenario: Viewer navigation
- **WHEN** 当前身份为 VIEWER
- **THEN** 前端只显示公开查看入口，不显示任务写入入口

### Requirement: Local test identity switcher
身份切换入口 SHALL 仅在 local/test 配置显示，并通过 API 客户端传递受控测试身份标识。

#### Scenario: Production hides switcher
- **WHEN** 前端运行在生产配置
- **THEN** 页面不显示测试身份切换入口
