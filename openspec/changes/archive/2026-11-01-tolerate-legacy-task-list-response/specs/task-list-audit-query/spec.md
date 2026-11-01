## MODIFIED Requirements

### Requirement: Task list readable business context

任务列表 SHALL 在既有身份范围内返回发起人显示名、业务说明、业务场景名称和数据库实例名称；界面 SHALL 展示这些字段，业务说明 SHALL 限制表格内占用宽度并可查看全文。列表响应 MUST NOT 包含数据库连接详情或凭据。当前端收到尚未包含新增展示字段的旧版列表响应时，SHALL 继续显示基础任务信息，并将缺失的新字段显示为占位符；已提供但类型错误的字段仍 MUST 拒绝。

#### Scenario: Task with associated scene and instance

- **WHEN** 授权用户查询有关联场景和实例的任务
- **THEN** 列表展示发起人名称、业务说明、场景名称和实例名称，不以内部 ID 替代名称

#### Scenario: Task without optional association

- **WHEN** 任务没有场景或实例关联
- **THEN** 对应列表单元格显示空值占位，不触发额外错误

#### Scenario: Frontend receives legacy task response

- **WHEN** 列表响应仍包含任务基本字段，但没有业务说明、发起人名称和关联名称
- **THEN** 页面继续显示任务及分页，新增列显示占位符

#### Scenario: Frontend receives malformed new field

- **WHEN** 响应提供了类型错误的业务说明或额外敏感字段
- **THEN** 前端拒绝该响应，不把错误内容当成可展示数据
