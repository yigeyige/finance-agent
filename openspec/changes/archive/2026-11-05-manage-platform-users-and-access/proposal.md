## Why

当前没有用户管理入口，角色及权限边界与计划中的普通用户工作台不一致。需要在保留任务和场景审计数据的前提下提供最小可用的账号管理和数据隔离。

## What Changes

- 新增管理员用户管理页面与接口，支持新增、编辑、停用、启用及密码重置；固定 admin 账号禁止操作。
- **BREAKING**：将业务角色收敛为 PLATFORM_ADMIN、PLATFORM_USER，更新数据库约束、权限守卫、导航和测试身份。
- 普通用户按发起人访问任务；场景全员可读，作者或管理员可维护。
- 最近异常按可信用户标识保存；普通用户读取本人快照，管理员读取全站快照。

## Capabilities

### New Capabilities

- `platform-user-management`: 用户管理、密码和账号保护。

### Modified Capabilities

- `identity-runtime-version`: 两角色模型和即时身份失效。
- `frontend-identity-navigation`: 管理员及普通用户菜单与路由。
- `task-list-audit-query`: 任务发起人归属边界。
- `latest-exception-stack-view`: 按用户隔离最近异常。
- `business-scene-skill-attachment`: 场景共享读取与作者维护。

## Impact

影响 IAM 角色/用户/密码、业务场景、任务访问守卫、最近异常捕获、Vue 工作台和 Flyway 迁移；不引入新依赖，不启动服务或部署。
