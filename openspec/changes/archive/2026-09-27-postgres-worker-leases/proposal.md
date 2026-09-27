## Why

当前任务步骤仓储已经具备 `FOR UPDATE SKIP LOCKED` 查询和活动租约唯一索引，但缺少将领取、租约生命周期、状态推进和 Worker 停止边界串成一个可复用运行时。没有统一的租约服务时，多个 Worker、心跳丢失或实例重启容易出现重复执行或等待人工任务占用执行资源。

## What Changes

- 增加事务化步骤领取服务：只领取已获批且可执行的步骤，并在同一事务内创建唯一活动租约和推进任务/步骤状态。
- 扩展租约仓储与领域校验，支持按持有者心跳、释放、过期租约安全接管，并保留数据库唯一索引作为最终并发防线。
- 增加可配置的 PostgreSQL Worker 调度器、有限线程池和优雅停止生命周期；停止领取后等待正在执行的安全点结束。
- 增加 Worker 配置绑定与边界校验，沿用现有 Hikari 数据源，不引入新的框架或外部依赖。
- 补充租约 SQL、状态、配置和生命周期契约测试；双实例并发由用户在本地环境手工验证。

## Capabilities

### New Capabilities

- `postgres-worker-runtime`: 定义 PostgreSQL Worker 的领取、租约生命周期、调度和优雅停止契约。

### Modified Capabilities

- `postgres-repository-transaction-boundary`: 补充领取事务、过期接管和持有者校验的数据库边界。
- `task-scope-step-model`: 补充等待人工状态不创建租约、运行步骤与活动租约绑定的约束。

## Impact

- 影响 `task` 应用服务、MyBatis-Plus 租约/步骤仓储、Spring 配置和 Worker 运行时包。
- 复用现有 PostgreSQL schema、部分唯一索引、Hikari 数据源和 Spring 生命周期 API，不新增 Maven 依赖或数据库迁移。
- 新 Worker 默认关闭，启用时使用独立的 Worker 线程池和 Hikari 连接池；API 继续使用 Web 容器线程池和 API 连接池。
