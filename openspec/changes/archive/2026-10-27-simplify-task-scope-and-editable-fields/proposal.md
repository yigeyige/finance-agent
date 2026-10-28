# Proposal

## Why

任务第二步字段过多，草稿、调查范围、任务前提仍携带处理系统、已知情况、信息来源、日期、处理内容、数据使用范围等冗余字段；第二步的字段也缺少更新能力。需要收敛字段并支持第二步编辑。

## What Changes

- **BREAKING**：业务处理草稿去掉 `systemCode`、`statement`、`sourceDescription`、`startDate`、`endDate`、`investigationAreas`、`dataUse`，仅保留处理对象与推荐方案/结论/证据。
- **BREAKING**：调查范围 `ScopeRevision` 去掉 `systemCode`、日期范围、处理内容；调查方案生成与任务详情同步精简。
- **BREAKING**：任务前提 `TaskPremise` 只保留处理对象。
- 新增任务更新接口：业务说明、业务场景 Skill、数据库实例可修改并持久化。
- 前端第二步仅保留处理对象、业务说明、业务场景 Skill、数据库实例（均可编辑）。
- 同步 `GUIDE_HANDLING` 提示词模板。

## Capabilities

### New Capabilities

（无新增能力）

### Modified Capabilities

- `business-handling-guidance`: 业务处理草稿字段清单收敛为处理对象、推荐方案、结论与证据。
- `guidance-draft-persistence`: 草稿持久化留痕字段同步收敛。
- `task-intake-scope-confirmation`: 调查范围仅保留处理对象；运维前提仅保留处理对象；支持更新任务基础信息。
- `task-intake-scope-ui`: 第二步精简并支持编辑业务说明、业务场景 Skill 与数据库实例。

## Impact

- 后端：`BusinessHandlingDraft`、`BusinessGuidanceRequest`、`BusinessModelResponse`、`StubBusinessModelClient`、`ScopeRevision`、`ConfirmScopeCommand`、`TaskIntakeApplicationService`、`ModelPlanContext`、`TaskDetailService`、`TaskPremise`、`AddTaskPremiseCommand`、`AddPremiseRequest`、`MybatisTaskMappings`、`TaskPremiseEntity`、任务更新接口与 `TaskBusinessSceneSkillRepository`。
- 数据库：`task.task_premise` 删除 `statement`/`source_description`；更新 `GUIDE_HANDLING`。
- 前端：`WorkbenchHome.vue`、`generated/index.ts`、`task-api.ts`、`business-workflow.ts`。
- 契约：草稿、范围、前提、任务更新接口字段变化。
