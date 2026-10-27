# Proposal

## Why

业务场景 Skill 当前把「业务说明」既当说明又当调查提示词使用，同时携带后续方案不再使用的「处理方式」「业务场景」两个字段；业务决策包生成需要独立的提示词，但缺少专门落点。需要收敛 Skill 字段并补齐决策包提示词，避免字段语义漂移和模型上下文冗余。

## What Changes

- **BREAKING**：业务场景 Skill 去掉 `handlingType`（处理方式）与 `premiseType`（业务场景）两个字段。
- **BREAKING**：`description`（业务说明）更名为 `investigationPrompt`（调查提示词）。
- 新增 `decisionPacketPrompt`（决策包提示词）字段。
- 模型受控 `sceneSkill` 上下文投影改为只携带 `name`、`investigationPrompt`、`decisionPacketPrompt`。
- Skill 维护列表与新增/编辑弹窗同步新字段；弹窗加宽，两个提示词编辑框加大。

## Capabilities

### New Capabilities

（无新增能力）

### Modified Capabilities

- `business-handling-guidance`: 业务场景 Skill 管理要求中的字段清单由「业务名称、业务说明、处理方式、业务场景」改为「场景名称、调查提示词、决策包提示词」。
- `business-model-interaction`: 模型读取任务关联 Skill 的受控字段由「名称、说明、处理方式、业务场景」改为「名称、调查提示词、决策包提示词」。

## Impact

- 后端：`BusinessSceneSkill` 领域对象/实体/请求/响应/服务/控制器、`BusinessSceneSkillContext`、`BusinessModelInteractionService`、`DeepSeekBusinessModelClient`、`StubBusinessModelClient`。
- 数据库：`task.business_scene_skill` 表结构迁移（重命名 `description` → `investigation_prompt`、删除 `handling_type`/`premise_type`、新增 `decision_packet_prompt`）。
- 前端：`frontend/src/api/generated/index.ts` 契约、`business-skill-api.ts`、`BusinessSkillCatalog.vue`、`WorkbenchHome.vue`（去掉对 `skill.premiseType` 的展示）。
- API：`/api/v1/business-scene-skills` 的读写字段发生变化。
