# role-permission-guard Specification

## Purpose
TBD - created by archiving change test-identity-permission-navigation. Update Purpose after archive.
## Requirements
### Requirement: Role permission matrix
服务端 SHALL 执行固定角色权限矩阵：OPERATOR 可操作任务，VIEWER 只读公开发布内容，PLATFORM_ADMIN 为应用内最高权限角色并拥有任务、配置、菜单及其他工作台角色权限；来源适用范围与字段策略仍由独立安全边界控制。

#### Scenario: Viewer writes task
- **WHEN** VIEWER 直接调用任务写接口
- **THEN** 服务端返回 403 且不改变任务状态

#### Scenario: Operator writes task
- **WHEN** OPERATOR 调用任务写接口且请求契约有效
- **THEN** 请求进入任务应用服务

#### Scenario: Platform administrator accesses workbench capability
- **WHEN** PLATFORM_ADMIN 请求任一已授权工作台任务或配置能力
- **THEN** 服务端按最高角色允许请求，同时仍执行字段策略和来源适用范围校验
