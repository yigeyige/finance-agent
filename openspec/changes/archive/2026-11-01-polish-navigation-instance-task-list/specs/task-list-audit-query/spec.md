## MODIFIED Requirements

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

## ADDED Requirements

### Requirement: Task list readable business context

任务列表 SHALL 在既有身份范围内返回发起人显示名、业务说明、业务场景名称和数据库实例名称；界面 SHALL 展示这些字段，业务说明 SHALL 限制表格内占用宽度并可查看全文。列表响应 MUST NOT 包含数据库连接详情或凭据。

#### Scenario: Task with associated scene and instance

- **WHEN** 授权用户查询有关联场景和实例的任务
- **THEN** 列表展示发起人名称、业务说明、场景名称和实例名称，不以内部 ID 替代名称

#### Scenario: Task without optional association

- **WHEN** 任务没有场景或实例关联
- **THEN** 对应列表单元格显示空值占位，不触发额外错误

### Requirement: Task list viewport scrolling

任务列表 SHALL 优先将纵向滚动限制在表格内容区域，并保持筛选操作区与分页器可见；可用高度不足时页面仍 SHALL 可访问全部控件。

#### Scenario: More rows than viewport fits

- **WHEN** 当前页行数超过视口剩余高度
- **THEN** 表格内容区域纵向滚动，分页器仍在表格下方可操作
