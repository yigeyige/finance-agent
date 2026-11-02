## MODIFIED Requirements

### Requirement: Controlled EMS metadata browsing
系统 SHALL 仅从所选启用 EMS 实例读取允许业务库的表与字段元数据；未选择实例时默认第一个启用实例。响应 MUST 仅包含 schema、表名、字段名、类型和注释等结构信息，且 MUST NOT 返回连接串、账号、密码、密文、样本行或被排除库的对象。表和字段注释 MUST 为字符串，且允许为空字符串。左侧表节点 SHALL 始终以表名展示，不因注释有无而改变主标题；表注释 SHALL 保留用于搜索和元数据展示。

#### Scenario: Administrator searches allowed tables
- **WHEN** 平台管理员按表名或表注释查询目录
- **THEN** 系统仅返回 `basedata`、`activiti_ems`、`account`、`budget`、`payment`、`quality`、`smart_audit` 与 `mobile` 中匹配的表，节点统一显示表名

#### Scenario: No EMS connection is enabled
- **WHEN** 平台管理员读取数据探索目录但没有启用 EMS 连接
- **THEN** 系统安全拒绝元数据读取，页面说明需要先启用连接且不泄露配置细节

#### Scenario: Metadata has no comment
- **WHEN** 允许业务库中的表或字段未配置注释，接口返回空字符串
- **THEN** 页面正常展示目录或字段结构，表节点仍显示表名，字段使用字段名作为回退展示文本

#### Scenario: Table has a comment
- **WHEN** 表配置了中文注释且管理员选择该表
- **THEN** 左侧节点只显示表名，内部选择仍保留真实注释与字段查询目标

### Requirement: Bounded interactive interface
系统 SHALL 使用既有 Vue 2、Element UI 与工作台样式提供 SQL 输入、表搜索、字段查看、结果页签和执行快捷方式。页面 MUST 提供实例下拉用于在启用实例间切换，且默认选择第一个启用实例；行数选择 MUST 仅提供 20、50、100，且不得绕过服务端配置的最大返回行数。宽屏目录树内容超出左侧可用高度时 SHALL 仅在左侧表列区纵向滚动，不因展开长 schema 撑高页面；窄屏 SHALL 保持全部表可访问。

#### Scenario: Administrator changes requested row limit
- **WHEN** 平台管理员在页面选择行数限制
- **THEN** 页面仅允许 20、50 或 100，并明确说明最终返回受启用连接配置限制

#### Scenario: Administrator views table fields
- **WHEN** 平台管理员在目录中选择允许的表
- **THEN** 页面展示该表字段名称、类型和注释，不执行数据查询

#### Scenario: Administrator switches database instance
- **WHEN** 平台管理员在数据探索页选择另一个启用实例
- **THEN** 页面重新加载所选实例的表目录，后续字段查看与查询均使用所选实例

#### Scenario: Schema contains many tables
- **WHEN** 管理员展开的 schema 表数量超出左侧可用高度
- **THEN** 左侧表列区内部出现纵向滚动条，右侧查询区及页面高度不随目录持续增长
