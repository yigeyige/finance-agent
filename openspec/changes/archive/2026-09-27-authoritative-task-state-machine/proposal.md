## Why

`TaskStatus` 已定义完整生命周期，但当前没有集中守卫合法转移，也没有应用服务保证状态更新、乐观版本和状态事件在同一事务中完成。后续任务发起、审批、Worker 和恢复都需要一个不依赖模型的权威状态入口。

## What Changes

- 新增纯领域状态机、转移表和暂停/恢复/取消守卫。
- 新增事务化任务状态服务，锁定任务、校验当前状态、按 `row_version` 更新并追加状态事件。
- 复用 `governance.audit_event` 持久化状态事件，不新增外部依赖或重复状态事件表。
- 增加状态覆盖矩阵、非法转移无副作用和绕过状态机的架构契约测试。

## Capabilities

### New Capabilities

- `authoritative-task-state-machine`: 定义任务状态、合法转移、暂停恢复、取消和状态事件事务边界。

### Modified Capabilities

- `task-scope-step-model`: 补充任务状态只能通过状态机应用服务推进的约束。

## Impact

- 影响 task 领域状态机、任务应用服务、任务状态事件仓储适配器和现有审计事件仓储。
- 复用 Spring 事务、MyBatis-Plus 仓储和现有 `row_version`；不新增依赖、不新增数据库表。
- 用户仍负责运行环境和人工状态事件抽查；本任务提供静态状态转移契约、编译和单元测试。
