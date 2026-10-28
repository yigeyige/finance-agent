# Design

## Context

任务第二步字段过多；草稿、范围、前提仍携带 systemCode、statement、sourceDescription、日期、处理内容、dataUse 等冗余字段；第二步字段也缺少更新能力。动机见 proposal.md。

## Goals / Non-Goals

**Goals:**

- 草稿、范围、前提字段收敛。
- 新增任务更新接口（业务说明、业务场景 Skill、数据库实例）。
- 前端第二步精简并可编辑。

**Non-Goals:**

- 不改 Skill 版本冻结、决策包模型分析。
- 不改范围确认的排除路径与字段权限（`excludedPaths`/`allowedReadFields`/`allowedRetainFields`/`allowedModelFields` 保留）。

## Decisions

1. **草稿字段收敛**
   - `BusinessHandlingDraft` 去掉 `systemCode`、`statement`、`sourceDescription`、`startDate`、`endDate`、`investigationAreas`、`dataUse`；保留 `subjectKey`、`objectKey`、`recommendedGoal`、`alternativeGoals`、`reason`、`conclusion`、`evidence`。

2. **范围与前提收敛**
   - `ScopeRevision` 去掉 `systemCode`、`effectiveTimeRange`、`includedPaths`；`ConfirmScopeCommand` 同步。
   - `TaskPremise` 去掉 `statement`、`sourceDescription`。

3. **下游精简**
   - `ModelPlanContext` 与 `generatePlan` 去掉 systemCode、日期范围、处理内容；`TaskDetailService.Scope`/`Premise` 同步。

4. **任务更新接口**
   - 新增 `PUT /api/v1/tasks/{taskId}`：`originalQuestion`、`businessSceneSkillId`、`databaseConfigId` 可修改。
   - `TaskBusinessSceneSkillRepository` 增加 `deleteByTaskId`，更新时先删后插。

5. **模板与迁移**
   - `V25__simplify_task_scope_fields.sql`：`task.task_premise` 删 `statement`/`source_description`；更新 `GUIDE_HANDLING` 模板。

## Risks / Trade-offs

- [严格 fromJson 字段数校验，模板未同步会导致模型输出被拒绝] → V25 迁移必须与草稿字段同批落地。
- [前提/范围删字段丢历史数据] → 用户已确认业务不再使用这些字段。

## Migration Plan

1. 新增 V25 迁移并触发 Flyway。
2. 后端同步草稿/范围/前提字段、下游与任务更新接口。
3. 前端同步第二步精简与编辑。
4. 运行后端编译/测试、前端类型检查/构建，重启验证。
