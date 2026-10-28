# Design

## Context

业务场景 Skill 已拆分为调查提示词与决策包提示词，但调查阶段仍同时注入两个提示词；`handlingType`/`premiseType` 仅从 Skill 实体移除，草稿、任务前提、前端与提示词模板仍保留。动机见 proposal.md。

## Goals / Non-Goals

**Goals:**

- 调查阶段 `sceneSkill` 只注入 `name + investigationPrompt`。
- 彻底移除 `handlingType`/`premiseType`（草稿、任务前提、前端、提示词模板、契约）。
- 保留 `decisionPacketPrompt` 于 `BusinessSceneSkillContext` 作为后续注入点。

**Non-Goals:**

- 不接入决策包模型分析，不消费 `decisionPacketPrompt`。
- 不处理 Skill 版本冻结（留给 `require-versioned-business-scene-skill`）。
- 不改 `TaskPremise` 除 `premiseType` 之外的其他前提字段。

## Decisions

1. **调查阶段只注入调查提示词**
   - `DeepSeekBusinessModelClient`（`guidanceInput`/`databaseQueryInput`）与 `StubBusinessModelClient`（`contextJson`）不再写入 `decisionPacketPrompt`。

2. **彻底移除 handlingType/premiseType**
   - `BusinessHandlingDraft` 去掉两个字段及 `HANDLING_TYPES` 校验，`fromJson`/`toJson`/`FIELDS` 同步。
   - 接口层 `BusinessGuidanceRequest.Draft`、`BusinessModelResponse.BusinessHandlingDraftResponse` 同步去掉。
   - `TaskPremise` 去掉 `premiseType`，同步 `AddTaskPremiseCommand`、`AddPremiseRequest`、`TaskIntakeApplicationService`、`TaskDetailService.Premise`、`MybatisTaskMappings`、`TaskPremiseEntity`。
   - 前端第二步去掉「处理方式」「业务场景」；第一步场景选择框改名「业务场景 Skill」。

3. **提示词模板同步**
   - 新增迁移更新 `governance.prompt_template` 的 `GUIDE_HANDLING`，去掉 `handlingType`/`premiseType` 输出要求与枚举约束，避免模型输出与严格 `fromJson` 字段数校验冲突。

4. **数据库迁移**
   - 新增 `V23__remove_handling_and_premise_fields.sql`：`task.task_premise` 删除 `premise_type` 列；`UPDATE governance.prompt_template` 更新 `GUIDE_HANDLING` 内容。

5. **前端契约**
   - `generated/index.ts` 去掉草稿/前提的 `handlingType`/`premiseType`，删除 `BusinessHandlingType`/`parseBusinessHandlingType`（无剩余消费者）。
   - `task-api.ts` 的 `AddPremisePayload` 去掉 `premiseType`。

## Risks / Trade-offs

- [严格 `fromJson` 用字段数校验，模板未同步会导致模型输出被拒绝] → 迁移必须与代码同步部署，模板更新与草稿字段同批落地。
- [task_premise.premise_type 删除丢历史前提类型数据] → 用户确认业务不再使用该字段，直接删除。
- [decisionPacketPrompt 暂时无消费方] → 字段保留，后续决策包任务接入。

## Migration Plan

1. 新增 V23 迁移（删 `premise_type` 列 + 更新 `GUIDE_HANDLING`）并触发 Flyway。
2. 后端同步草稿/前提字段与映射。
3. 前端同步表单与契约。
4. 运行后端编译/测试、前端类型检查/构建，重启验证。
