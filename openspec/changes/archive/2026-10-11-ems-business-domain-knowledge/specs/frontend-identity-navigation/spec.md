## ADDED Requirements

### Requirement: EMS business knowledge navigation
前端 SHALL 仅向具有 `TASK_READ` 权限的身份显示“EMS业务知识域”左侧菜单，并将该菜单路由至受权限守卫保护的业务知识域页面。

#### Scenario: Authorized user opens EMS business knowledge
- **WHEN** 具有 `TASK_READ` 权限的用户打开工作台左侧导航
- **THEN** 页面显示“EMS业务知识域”菜单，用户可进入业务知识域页面

#### Scenario: Unauthorized user opens knowledge deep link
- **WHEN** 不具有 `TASK_READ` 权限的用户访问 EMS业务知识域路由
- **THEN** 前端将其导回工作台，且后端仍拒绝目录接口请求
