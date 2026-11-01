## MODIFIED Requirements

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
