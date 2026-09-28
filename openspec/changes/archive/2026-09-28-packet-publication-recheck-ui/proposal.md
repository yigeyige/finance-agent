## Why

草稿复核后，来源数据可能已经变化，不能直接发布。发布命令必须重新枚举批准范围，发现差异时使草稿失效；无变化时要在同一事务写入不可变 revision、审计、outbox 和任务状态，并支持幂等重放。

## What Changes

- 新增发布前全量重查和原子发布服务，复用现有 `ScopeRecheckService` 与 `TransactionalPacketPublisher`。
- 新增发布 API，校验草稿、批准复核、canonical JSON/HTML 摘要和范围快照。
- 新增前端发布前重查表单，展示差异、stale 阻断和发布结果。

## Capabilities

### New Capabilities
- `packet-publication-recheck-ui`: 发布前重查和原子发布。

### Modified Capabilities

## Impact

- 影响 packet 发布应用服务、发布 REST 契约、任务状态和前端草稿页面。
- 不允许覆盖已发布 revision；重复使用 publication ID 返回原 revision。
