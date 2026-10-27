# Design

## Context

业务场景 Skill 当前字段：`skillId`、`name`、`description`、`handlingType`、`premiseType` 及审计字段，持久化于 `task.business_scene_skill`（V13 迁移）。模型链路通过 `BusinessSceneSkillContext` 把 `name/description/handlingType/premiseType` 作为 `sceneSkill` 注入模型输入。本次需求见 proposal.md。

## Goals / Non-Goals

**Goals:**

- Skill 字段收敛为 `name`（场景名称）、`investigationPrompt`（调查提示词）、`decisionPacketPrompt`（决策包提示词）。
- 前后端契约、模型上下文投影与 UI 同步新字段。
- 数据库迁移保留 `description` 现有数据到调查提示词，删除不再使用的两列。

**Non-Goals:**

- 不把 `decisionPacketPrompt` 接入业务决策包生成链路（仅新增字段并在 `sceneSkill` 上下文携带，后续 change 再消费）。
- 不改业务处理草稿 `BusinessHandlingDraft` 的 `handlingType`/`premiseType`（属草稿字段，非 Skill 字段）。
- 不改 Skill 版本化/任务冻结方案（由既有 `require-versioned-business-scene-skill` change 承担）。

## Decisions

1. **字段命名与语义**
   - `name` 保留，界面文案改为「场景名称」。
   - `description` → `investigationPrompt`（调查提示词），数据库列 `description` → `investigation_prompt`。
   - 新增 `decisionPacketPrompt`（决策包提示词），数据库列 `decision_packet_prompt`。
   - 备选：保留 `description` 只改文案——被否，因为需要与决策包提示词语义区分，字段名应明确。

2. **提示词长度**
   - 两个提示词字段采用 `varchar(2000)`，领域校验 `maxLength = 2000`。
   - 备选：维持 1000——被否，用户要求编辑框调大，提示词可能较长；2000 与 UI 放大匹配。

3. **数据库迁移**
   - 新增 `V21__business_scene_skill_prompts.sql`：
     - `RENAME COLUMN description TO investigation_prompt`（保留数据）。
     - `ALTER COLUMN investigation_prompt TYPE varchar(2000)`。
     - `ADD COLUMN decision_packet_prompt varchar(2000) NOT NULL DEFAULT ''`。
     - `DROP COLUMN handling_type`、`DROP COLUMN premise_type`。
   - 删除 `handling_type` 会连带删除 V13 的 CHECK 约束；V13 无依赖这两列的索引，安全。

4. **模型上下文投影**
   - `BusinessSceneSkillContext` 改为 `name/investigationPrompt/decisionPacketPrompt`。
   - `DeepSeekBusinessModelClient` 与 `StubBusinessModelClient` 的 `sceneSkill` 注入同步去掉 `handlingType/premiseType/description`，改为两个提示词字段。

5. **前端**
   - `BusinessSkillCatalog.vue` 列表列、表单、保存逻辑同步；弹窗 `width` 由 560px 调为 720px，两个 textarea `:rows` 由 4 调为 8。
   - `WorkbenchHome.vue` 去掉对 `skill.premiseType` 的展示，改为只展示 `skill.name`。

## Risks / Trade-offs

- [删除 `handling_type`/`premise_type` 丢失历史数据] → 用户已明确后续不用这两个字段；迁移直接 DROP，若需回滚需从备份恢复。
- [BREAKING 契约变更导致前后端短暂不一致] → 前后端同一 change 一并实施；本地先构建后端再验证前端契约。
- [Flyway 校验和] → 项目迁移为手动触发，迁移落库后需跑一次 repair 对齐 checksum（沿用既有约定）。

## Migration Plan

1. 后端新增 V21 迁移并编译。
2. 触发 Flyway 迁移（手动 `FlywayMigrationRunner`），执行后按需 repair。
3. 前端同步契约与 UI，运行类型检查与构建。
4. 本地重启后端，验证 `/api/v1/business-scene-skills` 读写字段与页面弹窗。
