## Why

任务、范围和步骤目前只有数据库表定义，缺少可复用的领域对象与仓储契约，无法安全地表达范围版本、审批绑定、租约并发和工具调用幂等边界。现在补齐这些模型，可以为后续任务状态机、运行时和前端工作台提供稳定的数据访问基础。

## What Changes

- 新增任务、范围修订、前提、调查步骤、步骤审批、任务租约和工具调用的 Java 领域模型。
- 新增基于 Spring JDBC 的显式 SQL 仓储与映射器，所有写入使用参数绑定。
- 用数据库约束和仓储契约保证范围不可原位修改、一步一审批、单任务单活动租约、乐观版本更新和调用幂等键唯一。
- 对三类范围字段使用带版本标识的 JSONB 值对象，避免未声明结构进入任务运行链路。
- 补充失败路径和约束验证测试，不改变现有迁移版本。

## Capabilities

### New Capabilities

- `task-scope-step-model`: 提供任务、范围、前提、步骤及其审批、租约和工具调用的可审计持久化能力。

### Modified Capabilities

<!-- No existing capability requirements are changed. -->

## Impact

- 影响 `backend` 的 task/application、task/domain、task/infrastructure 包及其测试。
- 复用现有 `spring-jdbc`、PostgreSQL 迁移 V3/V8 和 `JdbcTemplate` 数据源，不新增运行时依赖。
- 后续任务状态机和工具网关将依赖这些仓储接口；本变更不新增 HTTP API，不启动本地环境。
