## Why

领域仓储已经迁移到 MyBatis-Plus，但任务锁、租约领取、发布写入和只读查询仍缺少统一的事务边界与 PostgreSQL 锁语义。现在补齐这些边界，才能让后续状态机、Worker 和发布流程复用同一套数据库一致性约束。

## What Changes

- 增加任务行锁和就绪步骤领取的参数化 MyBatis SQL，使用 PostgreSQL `FOR UPDATE` / `SKIP LOCKED`。
- 增加事务化的租约领取、原子发布和只读查询服务边界。
- 将数据库唯一约束、乐观版本冲突和死锁/约束异常映射为明确的领域异常。
- 增加 SQL 契约、事务注解和失败回滚验证；不引入新依赖，不允许 Controller/Harness 直接访问 Mapper。

## Capabilities

### New Capabilities

- `postgres-repository-transaction-boundary`: 定义 PostgreSQL 仓储锁、事务、租约、发布和只读查询边界。

### Modified Capabilities

- `mybatis-plus-persistence`: 补充复杂 PostgreSQL SQL 必须集中在 Mapper、并由事务服务调用的约束。

## Impact

- 影响 task、packet、governance 的应用事务服务、Mapper 和领域异常。
- 复用现有 MyBatis-Plus 3.5.17、Spring `@Transactional` 和 PostgreSQL 迁移，不新增框架或依赖。
- 用户仍负责启动环境并执行真实并发/回滚验证；本任务提供静态 SQL 契约与编译测试。
