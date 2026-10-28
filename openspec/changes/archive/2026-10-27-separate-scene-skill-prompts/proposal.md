# Proposal

## Why

业务场景 Skill 调整为「调查提示词」与「决策包提示词」后，调查阶段仍同时注入两个提示词，导致两类提示词语义互相干扰；同时「处理方式/业务场景」字段只在 Skill 实体层被移除，模型草稿、任务前提、前端表单与提示词模板仍大面积保留，改动只做了一半。

## What Changes

- 调查阶段（初稿/修订、EMS 选表、查询规划）的 `sceneSkill` 仅注入 `name` 与 `investigationPrompt`，不再注入 `decisionPacketPrompt`。
- `decisionPacketPrompt` 保留在 `BusinessSceneSkillContext` 作为后续决策包模型分析的注入点，本次不消费。
- **BREAKING**：彻底移除 `handlingType`/`premiseType`：
  - 模型草稿 `BusinessHandlingDraft` 及其接口层 `Draft`/`BusinessHandlingDraftResponse`、`StubBusinessModelClient`。
  - 任务前提 `TaskPremise`、`AddTaskPremiseCommand`、`AddPremiseRequest`、`TaskIntakeApplicationService`、`TaskDetailService.Premise` 与持久化映射。
  - 前端工作台第二步「处理方式」「业务场景」字段，第一步场景选择框改名「业务场景 Skill」。
  - `governance.prompt_template` 的 `GUIDE_HANDLING` 模板去掉 `handlingType`/`premiseType` 输出要求。
- 数据库迁移：`task.task_premise` 删除 `premise_type` 列；更新 `GUIDE_HANDLING` 模板正文。

## Capabilities

### New Capabilities

（无新增能力）

### Modified Capabilities

- `business-model-interaction`: 调查阶段 sceneSkill 受控字段由「名称、调查提示词、决策包提示词」改为「名称、调查提示词」。
- `business-handling-guidance`: 业务处理草稿不再包含处理类型；场景 Skill 字段清单保持「场景名称、调查提示词、决策包提示词」。
- `guidance-draft-persistence`: 草稿持久化留痕不再包含处理类型。
- `task-intake-scope-confirmation`: 运维前提记录不再包含前提类型。
- `task-intake-scope-ui`: 两步向导不再展示处理方式/业务场景字段，第一步场景选择框文案为「业务场景 Skill」。
- `contract-validation-gate`: 契约校验不再校验处理类型枚举。

## Impact

- 后端：`BusinessHandlingDraft`、`BusinessGuidanceRequest`、`BusinessModelResponse`、`StubBusinessModelClient`、`TaskPremise`、`AddTaskPremiseCommand`、`AddPremiseRequest`、`TaskIntakeApplicationService`、`TaskDetailService`、`MybatisTaskMappings`、`TaskPremiseEntity`、`DeepSeekBusinessModelClient`。
- 数据库：`task.task_premise` 删列、`governance.prompt_template` 更新 `GUIDE_HANDLING`。
- 前端：`WorkbenchHome.vue`、`generated/index.ts`、`task-api.ts`、`business-skill-api.ts`。
- 契约：业务处理草稿/前提请求响应不再含 `handlingType`/`premiseType`。
