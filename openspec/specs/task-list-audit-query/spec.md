# task-list-audit-query Specification

## Purpose

为业务处理历史提供受身份范围约束的分页检索与跨任务只读审计详情，确保管理员和操作人员在既定权限内可靠追溯任务记录。

## Requirements

### Requirement: Identity-scoped paginated task query
系统 SHALL 提供按创建时间倒序、同时间以任务标识稳定排序的任务分页查询，支持标题关键字和任务状态筛选，默认每页 20 条且单页最多 100 条；响应 MUST 包含筛选身份范围内的准确总数、当前页、页大小和仅属于该页的任务项。单页任务项不得超过请求的页大小。

#### Scenario: Platform administrator queries historical tasks
- **WHEN** PLATFORM_ADMIN 按状态或标题关键字查询任一有效页码
- **THEN** 系统返回其筛选条件下全部任务范围中的对应页和准确总数，而不是固定最近 50 条

#### Scenario: Operator queries own tasks
- **WHEN** OPERATOR 查询任务列表
- **THEN** 系统仅在其发起的任务范围内执行筛选、计数和分页，不返回其他发起人的任务或总数

#### Scenario: Page sizes bound results
- **WHEN** 请求第一页并指定 `pageSize=10` 或 `pageSize=20`
- **THEN** 数据库只读取对应上限内的记录，响应 `total` 为筛选后全部记录数

#### Scenario: Later page preserves stable order
- **WHEN** 请求同一筛选条件下的第二页
- **THEN** 系统按创建时间和任务标识的固定倒序返回该页记录，不重复第一页记录

#### Scenario: Invalid page request is rejected
- **WHEN** 请求页码小于一或每页条数超过 100
- **THEN** 系统拒绝请求且不执行无界任务读取

### Requirement: Readonly task detail carries audit ownership
任务只读详情 SHALL 返回任务发起人标识，并继续按照可信身份限制访问；平台管理员可审计全部任务，普通操作人员只能读取自己发起的任务。

#### Scenario: Administrator audits another initiator task
- **WHEN** PLATFORM_ADMIN 从分页列表打开其他发起人的任务详情
- **THEN** 系统返回只读业务、范围、方案和发起人标识，不暴露密钥或可写入口

#### Scenario: Operator opens another initiator task
- **WHEN** 普通操作人员请求非本人发起的任务详情
- **THEN** 系统以不存在或无权访问处理，不泄露任务详情

### Requirement: Task list filter and action layout
系统 SHALL 在任务列表页面将业务标题和处理状态筛选字段展示在任务表格容器外部的独立筛选操作区；查询、重置和新建业务处理按钮 SHALL 位于该操作区的同一操作组中。页面 MUST 在标题后直接展示筛选操作区和任务表格，不显示“业务处理记录”小标题和旁侧数量。任务列表默认每页 SHALL 显示 10 条，并允许管理员选择 10、20、50 或 100 条；分页器 MUST 与服务端页码、条数和总数保持一致。

#### Scenario: Administrator filters and creates a business handling task
- **WHEN** 平台管理员进入任务列表
- **THEN** 系统在表格外展示业务标题与处理状态筛选字段，并在同一操作区展示查询、重置和新建业务处理按钮

#### Scenario: Operator reviews filtered task records
- **WHEN** 操作人员提交筛选条件或切换分页后双击某一任务行
- **THEN** 系统继续按其可信身份范围刷新任务记录，并打开该任务的只读详情

#### Scenario: Administrator opens the task list with default density
- **WHEN** 平台管理员首次进入任务列表且未调整分页条数
- **THEN** 系统按每页 10 条加载任务，并在分页器提供 10、20、50、100 条选择项

#### Scenario: Page size changes or result is empty

- **WHEN** 管理员切换每页条数或筛选后结果为空
- **THEN** 分页回到第一页，页码、条数和总数显示与服务端响应一致

### Requirement: Task list pagination controls align as one group

任务列表 SHALL 将总数、每页条数与翻页控件作为一组放在表格右下角；空列表时也 SHALL 保持相同的对齐方式。窄屏下控件 MUST 保持可访问。

#### Scenario: Empty task list

- **WHEN** 筛选结果为零条
- **THEN** 总数和每页条数控件位于表格右下角，不单独留在左侧

#### Scenario: Populated task list

- **WHEN** 任务列表有多页记录
- **THEN** 总数、每页条数和翻页按钮在右下角连续排列，分页请求行为保持不变

#### Scenario: Narrow viewport

- **WHEN** 视口不足以单行容纳分页控件
- **THEN** 控件允许换行且仍可操作

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

### Requirement: Task list viewport scrolling

任务列表 SHALL 优先将纵向滚动限制在表格内容区域，并保持筛选操作区与分页器可见；可用高度不足时页面仍 SHALL 可访问全部控件。

#### Scenario: More rows than viewport fits

- **WHEN** 当前页行数超过视口剩余高度
- **THEN** 表格内容区域纵向滚动，分页器仍在表格下方可操作
