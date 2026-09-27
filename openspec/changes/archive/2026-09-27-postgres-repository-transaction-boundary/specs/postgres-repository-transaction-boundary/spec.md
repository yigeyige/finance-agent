## ADDED Requirements

### Requirement: PostgreSQL repository lock and transaction boundaries

系统 SHALL 通过 MyBatis-Plus Mapper 和 Spring 事务服务集中实现 PostgreSQL 锁、写事务和只读事务；Controller/Harness 不得直接访问数据库。

#### Scenario: Task lock protects a multi-repository use case

- **WHEN** 应用服务开始推进任务或发布流程
- **THEN** 在同一写事务中先使用参数化 SQL 锁定任务行，再执行后续仓储操作

#### Scenario: Ready step claiming avoids worker blocking

- **WHEN** 多个 Worker 同时领取可执行步骤
- **THEN** 领取查询使用 `FOR UPDATE SKIP LOCKED` 和稳定排序，已被其他事务锁定的步骤被跳过

#### Scenario: Lease uniqueness remains the final concurrency guard

- **WHEN** 两个事务同时为同一任务创建活动租约
- **THEN** 数据库部分唯一索引只允许一个活动租约成功，失败事务映射为明确的活动租约冲突

#### Scenario: Publication is atomic

- **WHEN** 发布服务锁定任务并写入修订、审计、outbox 和任务状态
- **THEN** 所有写入位于同一事务，任一步失败都不留下半成品

#### Scenario: Read-only query has an explicit boundary

- **WHEN** 查询任务、租约或已发布修订
- **THEN** 通过 `readOnly=true` 事务服务执行，不隐式加入写事务

### Requirement: Repository failure mapping is explicit

系统 SHALL 将乐观版本冲突、活动租约唯一键冲突、PostgreSQL 可重试事务失败和其他约束错误映射为明确的领域基础设施异常，并保留 cause。

#### Scenario: Optimistic update conflict

- **WHEN** 任务行版本不再匹配期望版本
- **THEN** 仓储抛出并发操作异常，不静默返回成功

#### Scenario: Deadlock or serialization failure

- **WHEN** PostgreSQL 返回死锁或序列化失败
- **THEN** 映射为可识别的可重试仓储异常，交由上层策略决定是否重试
