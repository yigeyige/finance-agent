## Context

任务状态机已允许 `WAITING_STEP_APPROVAL -> RUNNING_STEP`，步骤仓储已有稳定排序的 `FOR UPDATE OF s, t SKIP LOCKED` 查询，`task_lease` 也已有活动租约部分唯一索引。现有缺口是：领取过程没有统一事务编排，租约更新没有持有者边界，过期租约不能原子接管，也没有 Worker 调度器及关闭语义。

实现必须兼容 Java 17、Spring Boot 4.0.4、Spring Framework 7.0.7 和 MyBatis-Plus 3.5.17，禁止新增依赖，禁止在 Worker 中执行长时间网络调用的同时持有数据库事务。API 与 Worker 在同一模块化单体中运行，因此通过独立的 Worker executor 和按线程路由的 Hikari 连接池隔离资源；事务只包围领取/心跳/释放等短数据库操作。

## Goals / Non-Goals

**Goals:**

- 以数据库行锁和部分唯一索引保证一个任务最多一个活动租约。
- 只领取 `WAITING_STEP_APPROVAL` 任务下已 `APPROVED` 的步骤；等待人工的步骤不进入 Worker。
- 提供领取、心跳、释放和过期接管的应用服务，以及可停止领取、等待运行任务结束的 Worker 生命周期。
- 对 Worker 线程数、轮询间隔、租约时长、心跳间隔和关闭等待时间做集中配置和边界校验。
- 以契约测试覆盖 SQL、租约持有者校验、时钟边界和关闭状态。

**Non-Goals:**

- 不实现具体工具调用、模型调用、checkpoint 或幂等意图；后续任务通过 Worker 处理器接入。
- 不创建第二套 MyBatis `SqlSessionFactory`；通过现有 MyBatis 数据源路由机制创建 API/Worker 两个 Hikari 连接池，并确保数据库操作短事务化。
- 不启动本地服务、不执行真实并发运行测试，也不修改既有 Flyway schema。

## Decisions

1. **领取事务集中在 `TaskLeaseService`。** 事务先调用步骤 `SKIP LOCKED` 查询，再创建租约、将步骤置为 `RUNNING` 并把任务推进到 `RUNNING_STEP`。任一步失败整体回滚；数据库唯一索引仍负责最后的重复领取保护。相比在 Worker 中分散调用多个 Repository，这能保持锁和状态变化的同一事务边界。
2. **过期接管按任务行串行化。** 接管服务先 `findByIdForUpdate` 锁任务，读取活动租约；只有 `expiresAt` 严格早于当前数据库时间才释放旧租约并创建新租约。当前租约未过期或时间相等时拒绝接管，避免时钟偏差导致双执行。应用传入的时间只作为已校验的观察时刻，不覆盖数据库时间语义。
3. **租约操作绑定 `ownerInstance`。** 心跳和释放必须同时匹配 lease id、owner instance 和未释放条件；更新行数为零即返回明确的租约丢失结果。这样旧 Worker 在接管后无法继续延长或释放新 Worker 的租约。
4. **Worker 使用 `SmartLifecycle` 和有界 executor。** 调度器只在 `finance-agent.worker.enabled=true` 时启动，周期性提交有限数量的领取任务；停止时先禁止新领取，再关闭 executor 并等待配置的宽限期，超时后仅记录受控告警，不强制中断正在执行的处理器。处理器接口留给后续任务实现。
5. **租约时长和心跳参数在配置层校验。** 心跳间隔必须小于租约时长，线程数、队列容量、轮询间隔和关闭宽限期必须为正且有上限，防止错误配置造成无界资源占用或必然过期。

## Risks / Trade-offs

- [Risk] Worker 进程在处理器完成前崩溃会留下活动租约 → 通过过期扫描/接管入口恢复；在过期前禁止并行恢复。
- [Risk] 本地时钟偏差造成过早接管 → 接管只接受严格过期，用户手工验证应使用数据库时间和边界样例。
- [Risk] 处理器耗时超过租约时长 → 后续处理器必须在安全点调用心跳；本任务只提供心跳接口，不自动延长未知执行。
- [Risk] 两个连接池会增加数据库连接总量 → 两个池均有显式上限，默认 Worker 池小于 API 池，后续容量量测再调整参数。

## Migration Plan

无需数据库迁移。部署时先保持 Worker 默认关闭，完成静态检查和用户并发验证后再通过 `finance-agent.worker.enabled` 与边界参数启用。回滚只需关闭 Worker 配置并保留已写入的租约历史；过期租约由人工或后续 Worker 接管。

## Open Questions

- 具体步骤处理器、工具调用和 checkpoint 由后续 `DEV-RUN-005` 及以后任务定义，本变更只提供可插拔处理器端口。
