## ADDED Requirements

### Requirement: Task list filter and action layout
系统 SHALL 在任务列表页面将业务标题和处理状态筛选字段展示在业务处理记录表格容器外部的独立筛选操作区；查询、重置和新建业务处理按钮 SHALL 位于该操作区的同一操作组中。页面 SHALL 提供任务概览区，并保持现有分页筛选和只读详情入口。

#### Scenario: Administrator filters and creates a business handling task
- **WHEN** 平台管理员进入任务列表
- **THEN** 系统在表格外展示业务标题与处理状态筛选字段，并在同一操作区展示查询、重置和新建业务处理按钮

#### Scenario: Operator reviews filtered task records
- **WHEN** 操作人员提交筛选条件或切换分页后双击某一任务行
- **THEN** 系统继续按其可信身份范围刷新任务记录，并打开该任务的只读详情
