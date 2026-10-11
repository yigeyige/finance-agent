## MODIFIED Requirements

### Requirement: Task list filter and action layout
系统 SHALL 在任务列表页面将业务标题和处理状态筛选字段展示在业务处理记录表格容器外部的独立筛选操作区；查询、重置和新建业务处理按钮 SHALL 位于该操作区的同一操作组中。页面 MUST 在标题后直接展示筛选操作区和业务处理记录，不得展示重复任务名称、数量或页码的总览卡片，并保持现有分页筛选和只读详情入口。任务列表默认每页 SHALL 显示 10 条，并允许管理员选择 10、20、50 或 100 条。

#### Scenario: Administrator filters and creates a business handling task
- **WHEN** 平台管理员进入任务列表
- **THEN** 系统在表格外展示业务标题与处理状态筛选字段，并在同一操作区展示查询、重置和新建业务处理按钮

#### Scenario: Operator reviews filtered task records
- **WHEN** 操作人员提交筛选条件或切换分页后双击某一任务行
- **THEN** 系统继续按其可信身份范围刷新任务记录，并打开该任务的只读详情

#### Scenario: Administrator opens the task list with default density
- **WHEN** 平台管理员首次进入任务列表且未调整分页条数
- **THEN** 系统按每页 10 条加载任务，并在分页器提供 10、20、50、100 条选择项
