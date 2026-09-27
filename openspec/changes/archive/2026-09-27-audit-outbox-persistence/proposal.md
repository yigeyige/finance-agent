## Why

数据库已经预留 `governance.audit_event` 和 `governance.outbox_event`，但后端没有可复用的领域契约和持久化入口，无法保证审计记录追加不可变、摘要链可验证、重复事件幂等以及审计与 outbox 同事务提交。现在补齐这些基础能力，避免后续发布和状态机代码各自实现一套审计逻辑。

## What Changes

- 增加带规范化 JSON payload、payload 摘要、前序摘要和事件摘要的审计事件领域模型。
- 增加带幂等键、投递状态和投递次数的本地 outbox 事件模型与仓储。
- 增加 MyBatis-Plus Entity、Mapper、显式映射和应用层仓储端口。
- 增加追加审计与 outbox 的事务边界，并补充数据库唯一约束、追加保护和待投递索引。
- 增加摘要重算、篡改检测、重复幂等键和迁移契约测试。

## Capabilities

### New Capabilities

- `audit-outbox-persistence`: 审计摘要链和本地 outbox 的领域、事务与持久化契约。

### Modified Capabilities

无。

## Impact

- 后端 `governance` 模块新增 domain/application/infrastructure 类型。
- 新增 Flyway V10 迁移，增强既有审计和 outbox 表约束与索引。
- 扩展 MyBatis-Plus Mapper 扫描和 UUID 策略契约测试。
- 不新增第三方依赖，不连接或启动外部运行环境。
