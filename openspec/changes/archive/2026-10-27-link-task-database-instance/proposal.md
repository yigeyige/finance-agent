# Proposal

## Why

模型分析当前默认使用第一个启用的 EMS 实例，无法按任务选择；任务创建时也没有数据库实例的可选入口。需要让任务可关联数据库实例，并让模型分析按任务所选实例执行，未选实例时不执行数据库查询。

## What Changes

- **BREAKING**：任务支持可选的数据库实例并在创建时持久化；未勾选实例时模型分析不执行 EMS 数据库查询。
- 新增 OPERATOR 可读的「启用数据库实例列表」接口（不含密码/密文），供第一步选择。
- 模型分析链路按任务所选数据库实例执行 EMS 辅助查询。

## Capabilities

### New Capabilities

（无新增能力）

### Modified Capabilities

- `ems-database-connection-analysis`: 新增 OPERATOR 可读的启用实例列表；模型分析的 EMS 辅助查询按任务所选实例执行，未选实例时不查询。
- `task-intake-scope-confirmation`: 任务创建支持可选数据库实例。

## Impact

- 后端：`InvestigationTask`、`InvestigationTaskEntity`、`MybatisTaskMappings`、`CreateTaskRequest`、`CreateTaskDraftCommand`、`TaskIntakeController`、`TaskIntakeApplicationService`、`TaskStateTransitionService`、`PacketPublicationService`、`EmsDatabaseAssistanceService`、`BusinessModelInteractionService`、`EmsDatabaseConnectionConfigController`、任务更新接口。
- 数据库：`task.investigation_task` 新增 `database_config_id`。
- 前端：`WorkbenchHome.vue` 第一步新增实例选择与第二步修改、`generated/index.ts`、`task-api.ts`。
- 契约：任务创建/更新接口增加 `databaseConfigId`；新增启用实例列表接口。
