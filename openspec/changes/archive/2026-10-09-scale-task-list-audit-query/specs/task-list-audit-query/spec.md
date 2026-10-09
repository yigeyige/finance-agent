## ADDED Requirements

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
