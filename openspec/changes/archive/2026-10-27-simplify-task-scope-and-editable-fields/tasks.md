# Tasks

## 1. 数据库迁移

- [x] 1.1 新增 `V25__simplify_task_scope_fields.sql`：`task.task_premise` 删 `statement`/`source_description`，更新 `GUIDE_HANDLING` 模板；验证 Flyway 迁移通过

## 2. 后端草稿字段精简

- [x] 2.1 调整 `BusinessHandlingDraft`：去掉 systemCode/statement/sourceDescription/startDate/endDate/investigationAreas/dataUse；验证编译通过
- [x] 2.2 同步 `BusinessGuidanceRequest.Draft`、`BusinessModelResponse.BusinessHandlingDraftResponse`、`StubBusinessModelClient`；验证编译通过

## 3. 后端范围/前提字段精简

- [x] 3.1 调整 `ScopeRevision`、`ConfirmScopeCommand`、`TaskIntakeApplicationService.confirmScope`、`MybatisTaskMappings`；验证编译通过
- [x] 3.2 调整 `ModelPlanContext`、`BusinessModelInteractionService.generatePlan`、`TaskDetailService.Scope`；验证编译通过
- [x] 3.3 调整 `TaskPremise`、`AddTaskPremiseCommand`、`AddPremiseRequest`、`TaskDetailService.Premise`、`MybatisTaskMappings`、`TaskPremiseEntity`；验证编译通过

## 4. 后端任务更新接口

- [x] 4.1 `TaskBusinessSceneSkillRepository` 增加 `deleteByTaskId` 及 MyBatis 实现；验证编译通过
- [x] 4.2 新增 `UpdateTaskRequest` 与 `TaskIntakeApplicationService.updateTask`、`TaskIntakeController` `PUT /tasks/{taskId}`；验证编译通过

## 5. 前端两步表单与契约

- [x] 5.1 调整 `WorkbenchHome.vue`：第二步仅保留处理对象/业务说明/业务场景 Skill/数据库实例并可编辑；验证类型检查通过
- [x] 5.2 调整 `generated/index.ts`、`task-api.ts`、`business-workflow.ts` 契约；验证类型检查通过

## 6. 验证与收尾

- [x] 6.1 运行后端编译与相关测试、前端类型检查与构建，记录结果
- [x] 6.2 重启本地后端，验证草稿/范围/前提契约与第二步编辑

## Workflow follow-up

- 同步 delta specs 到 `openspec/specs/` 并归档 change。
- 更新任务目标勾选与执行记录，并按项目流程提交。
