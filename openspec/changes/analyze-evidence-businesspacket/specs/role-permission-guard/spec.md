## MODIFIED Requirements

### Requirement: Role permission matrix
服务端 SHALL 执行固定角色权限矩阵：OPERATOR 可操作任务，并可生成和读取内部 `businesspacket`、evidence manifest、分块分析与累积证据；VIEWER 只读具有独立公开发布契约的内容，MUST NOT 访问业务决策包或内部证据；PLATFORM_ADMIN 为应用内最高权限角色并拥有任务、配置、菜单及其他工作台角色权限。来源适用范围与字段策略仍由独立安全边界控制。

#### Scenario: Viewer writes task
- **WHEN** VIEWER 直接调用任务写接口
- **THEN** 服务端返回 403 且不改变任务状态

#### Scenario: Operator writes task
- **WHEN** OPERATOR 调用任务写接口且请求契约有效
- **THEN** 请求进入任务应用服务

#### Scenario: Operator accesses business decision artifacts
- **WHEN** OPERATOR 请求生成或读取当前授权任务的业务决策包、manifest、分块分析或累积证据
- **THEN** 服务端在继续执行任务范围和字段策略校验后允许请求

#### Scenario: Viewer accesses business decision artifacts
- **WHEN** VIEWER 直接请求业务决策包、manifest、分块分析或累积证据
- **THEN** 服务端返回 403 且不泄露摘要、计数或内部元数据

#### Scenario: Platform administrator accesses workbench capability
- **WHEN** PLATFORM_ADMIN 请求任一已授权工作台任务或配置能力
- **THEN** 服务端按最高角色允许请求，同时仍执行字段策略和来源适用范围校验
