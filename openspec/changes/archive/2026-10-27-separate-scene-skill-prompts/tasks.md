# Tasks

## 1. 数据库迁移

- [x] 1.1 新增 `V23__remove_handling_and_premise_fields.sql`：删除 `task.task_premise.premise_type` 列并更新 `GUIDE_HANDLING` 模板去掉 handlingType/premiseType；验证 Flyway 迁移通过

## 2. 后端草稿去处理方式/业务场景

- [x] 2.1 修改 `BusinessHandlingDraft`：去掉 handlingType/premiseType、`HANDLING_TYPES`、`FIELDS` 及 fromJson/toJson 对应字段；验证编译通过
- [x] 2.2 同步 `BusinessGuidanceRequest.Draft` 与 `toCurrentDraft`；验证编译通过
- [x] 2.3 同步 `BusinessModelResponse.BusinessHandlingDraftResponse` 与 `Guidance.from`；验证编译通过
- [x] 2.4 修改 `StubBusinessModelClient`：不再生成/判断 handlingType/premiseType；验证编译通过

## 3. 后端任务前提去业务场景

- [x] 3.1 修改 `TaskPremise`：去掉 premiseType；验证编译通过
- [x] 3.2 同步 `AddTaskPremiseCommand`、`AddPremiseRequest`、`TaskIntakeController`、`TaskIntakeApplicationService.addPremise`；验证编译通过
- [x] 3.3 同步 `TaskDetailService.Premise`、`MybatisTaskMappings`、`TaskPremiseEntity`；验证编译通过

## 4. 前端表单与契约

- [x] 4.1 修改 `WorkbenchHome.vue`：第二步去掉处理方式/业务场景，第一步场景选择框改名「业务场景 Skill」，Form/applyGuidance 去掉对应字段；验证前端类型检查通过
- [x] 4.2 修改 `generated/index.ts`：草稿/前提去掉 handlingType/premiseType，删除 BusinessHandlingType/parseBusinessHandlingType；验证类型检查通过
- [x] 4.3 同步 `task-api.ts` 的 AddPremisePayload 与 `business-skill-api.ts` 导出；验证类型检查通过

## 5. 验证与收尾

- [x] 5.1 运行后端编译与相关测试、前端类型检查与构建，记录结果
- [x] 5.2 重启本地后端，验证业务处理草稿/前提接口与页面不再含 handlingType/premiseType

## Workflow follow-up

- 同步 delta specs 到 `openspec/specs/` 并归档 change。
- 更新任务目标勾选与执行记录，并按项目流程提交。
