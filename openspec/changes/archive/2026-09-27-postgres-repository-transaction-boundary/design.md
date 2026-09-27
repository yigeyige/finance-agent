## Context

当前持久层统一使用 MyBatis-Plus 3.5.17，实体和仓储接口已覆盖任务、租约、决策包、审计与 outbox。任务的核心缺口不是 JDBC API，而是跨仓储调用缺少明确的 Spring 事务边界，以及锁语义没有集中在 Mapper SQL 中。

## Decisions

### 1. 使用 MyBatis-Plus Mapper 承载显式 PostgreSQL SQL

- 常规 CRUD 继续复用 `BaseMapper`。
- 任务行锁、就绪步骤领取等复杂查询使用 Mapper `@Select` 参数化 SQL；不在 Service/Controller 拼接 SQL。
- 领取 SQL 使用 `FOR UPDATE SKIP LOCKED`，按步骤创建时间和 ID 稳定排序，避免多个 Worker 互相等待。

### 2. 事务边界放在应用服务

- 任务锁查询只能由带 `@Transactional` 的任务事务服务调用。
- 租约领取在一个写事务中完成：锁定任务、插入租约；活动租约部分唯一索引继续承担最终并发防线。
- 发布事务在一个写事务中完成：锁任务、校验 READY_TO_PUBLISH、插入不可变修订、追加审计和 outbox、乐观版本更新任务；任何一步失败整体回滚。
- 只读查询通过独立 `@Transactional(readOnly = true)` 服务入口执行，避免把只读查询和写事务混用。

### 3. 异常映射

- 乐观版本更新为 0 行时抛出 `ConcurrentRepositoryOperationException`。
- 活动租约唯一键冲突映射为 `ActiveLeaseConflictException`。
- PostgreSQL 死锁/序列化失败保留可识别的 `RetryableRepositoryException`，不在本任务内自动重试；上层 Worker 后续决定重试预算。
- 其他数据完整性错误映射为 `RepositoryConstraintViolationException`，保留原始 cause 供日志与诊断使用，不向 API 暴露 SQL 或连接信息。

### 4. 发布输入与后续扩展

本任务提供最小 `PublishTransactionCommand`，由后续发布用例负责完成草稿、复核和范围重查，事务服务只接受已完成边界校验的领域对象，避免提前实现完整发布状态机。

## Rejected Alternatives

- 不重新引入 `JdbcTemplate`：项目技术基线已锁定 MyBatis-Plus，新增 JDBC 访问会重复建设并破坏现有 Mapper 扫描契约。
- 不在每个 Repository 方法上随意添加事务：事务必须围绕完整用例，避免跨仓储操作出现部分提交。
- 不在本任务实现自动死锁重试：重试需要结合 Worker 租约、预算和幂等策略，留给运行时任务。

## Verification

- Mapper SQL 契约测试检查 `FOR UPDATE`、`SKIP LOCKED`、参数占位和排序边界。
- 服务单元测试检查事务注解、状态守卫、异常映射和失败顺序。
- Maven 编译与相关测试通过；真实数据库并发、死锁和回滚由用户手动执行。
