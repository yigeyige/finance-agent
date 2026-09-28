# role-permission-guard Specification

## Purpose
TBD - created by archiving change test-identity-permission-navigation. Update Purpose after archive.
## Requirements
### Requirement: Role permission matrix
服务端 SHALL 执行固定权限矩阵：OPERATOR 可操作任务，VIEWER 只读公开发布内容，PLATFORM_ADMIN 可管理配置但不自动获得源范围权限。

#### Scenario: Viewer writes task
- **WHEN** VIEWER 直接调用任务写接口
- **THEN** 服务端返回 403 且不改变任务状态

#### Scenario: Operator writes task
- **WHEN** OPERATOR 调用任务写接口且请求契约有效
- **THEN** 请求进入任务应用服务

#### Scenario: Platform admin lacks source range
- **WHEN** PLATFORM_ADMIN 请求源范围数据
- **THEN** 服务端仍按源范围授权单独拒绝或要求额外授权

