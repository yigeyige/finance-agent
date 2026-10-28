# Tasks

## 1. 数据库迁移

- [x] 1.1 新增 `V24__task_database_instance.sql`：`task.investigation_task` 加 `database_config_id`；验证 Flyway 迁移通过

## 2. 后端任务-数据库实例打通

- [x] 2.1 新增 OPERATOR 可读的启用实例列表接口（仅标识与名称）；验证接口返回不含连接配置/凭据
- [x] 2.2 调整 `InvestigationTask`、`InvestigationTaskEntity`、`MybatisTaskMappings` 增加 `databaseConfigId`；同步 `TaskStateTransitionService`、`PacketPublicationService` 等构造点；验证编译通过
- [x] 2.3 调整 `CreateTaskRequest`、`CreateTaskDraftCommand`、`TaskIntakeController`、`TaskIntakeApplicationService.createTask` 支持可选 `databaseConfigId`；验证编译通过
- [x] 2.4 调整 `EmsDatabaseAssistanceService.collect` 增加 `databaseConfigId` 参数（空则跳过）；调整 `BusinessModelInteractionService.guideBusinessHandling` 传入任务实例；验证编译通过

## 3. 前端两步表单与契约

- [x] 3.1 调整 `WorkbenchHome.vue`：第一步加数据库实例选择并随创建任务提交；验证类型检查通过
- [x] 3.2 调整 `generated/index.ts`、`task-api.ts` 契约：任务创建加 `databaseConfigId`、启用实例列表接口；验证类型检查通过

## 4. 验证与收尾

- [x] 4.1 运行后端编译与相关测试、前端类型检查与构建，记录结果
- [x] 4.2 重启本地后端，验证任务创建选择实例后模型分析按实例、未选实例不查询、启用实例列表可用

## Workflow follow-up

- 同步 delta specs 到 `openspec/specs/` 并归档 change。
- 更新任务目标勾选与执行记录，并按项目流程提交。
