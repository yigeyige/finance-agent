## MODIFIED Requirements

### Requirement: Task list filter and action layout
系统 SHALL 在任务列表页面将业务标题和处理状态筛选字段展示在业务处理记录表格容器外部的独立筛选操作区；查询、重置和新建业务处理按钮 SHALL 位于该操作区的同一操作组中。页面 SHALL 提供紧凑的任务概览区，并保持现有分页筛选和只读详情入口。任务列表默认每页 SHALL 显示 10 条，并允许管理员选择 10、20、50 或 100 条。

#### Scenario: Administrator opens the task list with default density
- **WHEN** 平台管理员首次进入任务列表且未调整分页条数
- **THEN** 系统按每页 10 条加载任务，并在分页器提供 10、20、50、100 条选择项
