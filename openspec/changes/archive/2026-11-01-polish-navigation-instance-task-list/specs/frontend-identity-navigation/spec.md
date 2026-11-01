## MODIFIED Requirements

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
