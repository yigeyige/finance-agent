# task-list-audit-query Specification

## Purpose

为业务处理历史提供受身份范围约束的分页检索与跨任务只读审计详情，避免固定条数截断影响追溯。

## Requirements

### Requirement: Identity-scoped paginated task query
系统 SHALL 提供按创建时间倒序的任务分页查询，支持标题关键字和任务状态筛选，默认每页 20 条且单页最多 100 条；响应 MUST 包含总数、当前页、页大小和任务项。

#### Scenario: Platform administrator queries historical tasks
- **WHEN** PLATFORM_ADMIN 按状态或标题关键字查询任一有效页码
- **THEN** 系统返回其筛选条件下全部任务范围中的对应页和准确总数，而不是固定最近 50 条

#### Scenario: Operator queries own tasks
- **WHEN** OPERATOR 查询任务列表
- **THEN** 系统仅在其发起的任务范围内执行筛选和分页，不返回其他发起人的任务或总数

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
系统 SHALL 在任务列表页面将业务标题和处理状态筛选字段展示在业务处理记录表格容器外部的独立筛选操作区；查询、重置和新建业务处理按钮 SHALL 位于该操作区的同一操作组中。页面 SHALL 提供紧凑的任务概览区，并保持现有分页筛选和只读详情入口。任务列表默认每页 SHALL 显示 10 条，并允许管理员选择 10、20、50 或 100 条。

#### Scenario: Administrator filters and creates a business handling task
- **WHEN** 平台管理员进入任务列表
- **THEN** 系统在表格外展示业务标题与处理状态筛选字段，并在同一操作区展示查询、重置和新建业务处理按钮

#### Scenario: Operator reviews filtered task records
- **WHEN** 操作人员提交筛选条件或切换分页后双击某一任务行
- **THEN** 系统继续按其可信身份范围刷新任务记录，并打开该任务的只读详情

#### Scenario: Administrator opens the task list with default density
- **WHEN** 平台管理员首次进入任务列表且未调整分页条数
- **THEN** 系统按每页 10 条加载任务，并在分页器提供 10、20、50、100 条选择项
