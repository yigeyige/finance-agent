## MODIFIED Requirements

### Requirement: Controlled EMS metadata browsing
系统 SHALL 仅从当前启用 EMS 连接读取允许业务库的表与字段元数据。响应 MUST 仅包含 schema、表名、字段名、类型和注释等结构信息，且 MUST NOT 返回连接串、账号、密码、密文、样本行或被排除库的对象。表和字段注释 MUST 为字符串，且允许为空字符串。

#### Scenario: Administrator searches allowed tables
- **WHEN** 平台管理员按表名或表注释查询目录
- **THEN** 系统仅返回 `basedata`、`activiti_ems`、`account`、`budget`、`payment`、`quality`、`smart_audit` 与 `mobile` 中匹配的表

#### Scenario: No EMS connection is enabled
- **WHEN** 平台管理员读取数据探索目录但没有启用 EMS 连接
- **THEN** 系统安全拒绝元数据读取，页面说明需要先启用连接且不泄露配置细节

#### Scenario: Metadata has no comment
- **WHEN** 允许业务库中的表或字段未配置注释，接口返回空字符串
- **THEN** 页面正常展示目录或字段结构，并使用表名或字段名作为回退展示文本
