## MODIFIED Requirements

### Requirement: Lease and optimistic task concurrency

系统 SHALL 支持任务行版本更新和任务租约管理，且一个任务同时最多存在一个未释放租约。租约只能绑定当前正在执行的步骤；等待人工确认、未获批或终止状态的任务不得持有活动租约。

#### Scenario: Optimistic update detects conflict
- **WHEN** 两个请求基于同一个 row version 更新任务
- **THEN** 只有一个更新成功，另一个返回并发冲突且不覆盖已提交状态

#### Scenario: Second active lease is rejected
- **WHEN** 任务已有未释放租约时再次获取租约
- **THEN** 数据库部分唯一索引拒绝第二个活动租约，应用返回活动租约冲突

#### Scenario: Human waiting task has no lease
- **WHEN** 任务等待步骤审批或专业确认
- **THEN** 任务不创建活动租约，Worker 轮询不会占用该任务的执行槽位
