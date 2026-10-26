# Proposal

## Why

已沉淀的 EMS 表知识（`docs/ems-knowledge/table-knowledge/*.json`）尚未接入运行时；业务场景 Skill 没有数据库关联，`description` 上限 1000 字装不下表知识；EMS 连接全局唯一启用，业务处理无法区分「查哪个库」。需要把表知识入库并关联到 Skill，让业务处理通过 Skill 明确查询目标库并注入对应表知识。

## What Changes

- 新增 `ems_table_knowledge` 表：按「数据库连接实例 + schema + 表」存储表知识（表注释、数据状态、businessMeaning/keyFields/relations/promptHint 结构化 JSON）。
- `business_scene_skill` 增加可空 `database_config_id` 列：Skill 至多关联一个数据库连接实例。
- Skill 增改接口支持选择数据库，`BusinessSceneSkillContext` 携带关联的数据库标识。
- 业务处理创建任务时 Skill 改为 **必选**（**BREAKING**）；历史无 Skill 任务降级为不查库的纯文本分析。
- 模型在**选表与生成 SQL 两阶段**都按 Skill 关联的数据库查询，并注入对应表知识。
- `skill.description` 回归纯业务说明，不再承载表知识。
- 模型辅助查询不再使用全局唯一启用连接，改用任务关联 Skill 的数据库（**BREAKING**）。
- 将现有 3 个 table-knowledge JSON 导入 `ems_table_knowledge`。

## Capabilities

### New Capabilities
- `ems-table-knowledge`: 按数据库连接实例存储、关联并注入 EMS 表级结构化知识。

### Modified Capabilities
- `business-model-interaction`: 业务处理初稿要求关联有效业务场景 Skill，未关联时降级纯文本。
- `ems-database-connection-analysis`: 模型辅助查询改用任务关联 Skill 的数据库，而非全局唯一启用连接。

## Impact

- 后端：`task.business_scene_skill`、新增 `governance.ems_table_knowledge`、`EmsDatabaseAssistanceService`、`BusinessModelInteractionService`、`BusinessSceneSkillContext`、`BusinessSceneSkill` 域与仓储、Flyway 迁移。
- 前端：Skill 管理页（数据库选择）、`WorkbenchHome.vue`（Skill 必选）。
- 契约：`/v1/business-scene-skills` 请求/响应增加数据库标识；创建任务请求行为变更（Skill 必选）。
