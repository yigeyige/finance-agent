# 数据探索

## Purpose

为财务运维工作台的平台管理员提供基于已启用 EMS 连接的受控只读数据探索能力。

## Requirements

### Requirement: Protected data exploration workbench
系统 SHALL 在财务运维工作台提供“数据探索”路由和菜单入口，且仅 `PLATFORM_ADMIN` 身份可以查看、访问和调用该能力。

#### Scenario: Platform administrator opens data exploration
- **WHEN** 具有 `PLATFORM_ADMIN` 角色的用户进入数据探索页面
- **THEN** 系统展示当前数据源状态、受控表目录、SQL 输入区和结果区域

#### Scenario: Non-administrator accesses data exploration
- **WHEN** 不具有 `PLATFORM_ADMIN` 角色的身份请求数据探索页面或接口
- **THEN** 前端不展示入口且服务端拒绝请求

### Requirement: Controlled EMS metadata browsing
系统 SHALL 仅从所选启用 EMS 实例读取允许业务库的表与字段元数据；未选择实例时默认第一个启用实例。响应 MUST 仅包含 schema、表名、字段名、类型和注释等结构信息，且 MUST NOT 返回连接串、账号、密码、密文、样本行或被排除库的对象。表和字段注释 MUST 为字符串，且允许为空字符串。

#### Scenario: Administrator searches allowed tables
- **WHEN** 平台管理员按表名或表注释查询目录
- **THEN** 系统仅返回 `basedata`、`activiti_ems`、`account`、`budget`、`payment`、`quality`、`smart_audit` 与 `mobile` 中匹配的表

#### Scenario: No EMS connection is enabled
- **WHEN** 平台管理员读取数据探索目录但没有启用 EMS 连接
- **THEN** 系统安全拒绝元数据读取，页面说明需要先启用连接且不泄露配置细节

#### Scenario: Metadata has no comment
- **WHEN** 允许业务库中的表或字段未配置注释，接口返回空字符串
- **THEN** 页面正常展示目录或字段结构，并使用表名或字段名作为回退展示文本

### Requirement: Hard read-only interactive query execution
系统 SHALL 仅允许平台管理员通过数据探索执行一条符合既有 EMS 只读 SQL Guard 的 `SELECT`。执行 MUST 使用所选启用实例的查询超时、最大返回行数和受限结果投影，且 MUST NOT 将原始 SQL、结果或凭据持久化。

#### Scenario: Administrator executes a valid select
- **WHEN** 平台管理员提交通过 Guard 校验的单条、库名限定 `SELECT`
- **THEN** 系统返回受限列、行、结果行数与执行耗时，并在页面表格中展示结果

#### Scenario: Administrator submits a forbidden statement
- **WHEN** 平台管理员提交写入、DDL、多语句、注释、非允许库或其他 Guard 拒绝的 SQL
- **THEN** 系统拒绝执行且页面显示安全、可操作的中文提示，不访问 EMS 数据库

#### Scenario: Query has no result rows
- **WHEN** 合法查询未返回任何记录
- **THEN** 系统返回空结果并在页面展示“未查询到数据”状态

### Requirement: Bounded interactive interface
系统 SHALL 使用既有 Vue 2、Element UI 与工作台样式提供 SQL 输入、表搜索、字段查看、结果页签和执行快捷方式。页面 MUST 提供实例下拉用于在启用实例间切换，且默认选择第一个启用实例；行数选择 MUST 仅提供 20、50、100，且不得绕过服务端配置的最大返回行数。

#### Scenario: Administrator changes requested row limit
- **WHEN** 平台管理员在页面选择行数限制
- **THEN** 页面仅允许 20、50 或 100，并明确说明最终返回受启用连接配置限制

#### Scenario: Administrator views table fields
- **WHEN** 平台管理员在目录中选择允许的表
- **THEN** 页面展示该表字段名称、类型和注释，不执行数据查询

#### Scenario: Administrator switches database instance
- **WHEN** 平台管理员在数据探索页选择另一个启用实例
- **THEN** 页面重新加载所选实例的表目录，后续字段查看与查询均使用所选实例
