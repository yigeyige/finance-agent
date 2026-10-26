# Design

## Context

- 表知识已沉淀为 `docs/ems-knowledge/table-knowledge/*.json`（按 schema：basedata/account/activiti_ems），但未接入运行时。
- `task.business_scene_skill` 无数据库关联，`description` 上限 1000 字，装不下表知识。
- `task.ems_database_connection_config` 支持多实例但全局唯一启用；`EmsDatabaseAssistanceService` 与 `DataExplorationService` 都用 `requireEnabled()` 取唯一连接。
- 模型辅助查询链路：`EmsDatabaseAssistanceService.collect(modelClient, context, progress)` → `coarseFilter(listAllowedTables(config))` → `selectRelevantTables` → `listColumns` → `planEmsDatabaseQuery` → `execute`。

## Goals / Non-Goals

**Goals**
- 表知识按「数据库连接实例 + schema + 表」入库，可查询、可标记无数据。
- Skill 至多关联一个数据库连接实例，业务处理通过 Skill 确定查询目标库。
- 模型在选表与生成 SQL 两个阶段都注入所选库的表知识，替换硬塞 `description` 的做法。

**Non-Goals**
- 不改动 `data-exploration`（数据探索）的全局单连接行为。
- 不做表知识的运行时自动更新/重采集（本次只做一次性导入 + 手工维护入口可后置）。
- 不做多公司 EMS 数据的跨库关联合并（Skill 至多一个库）。

## Decisions

### 1. 表知识入库：新建 `governance.ems_table_knowledge`
- 列：`config_id`（连接实例 UUID）、`schema`、`table`、`table_comment`、`data_status`（`DATA`/`EMPTY`）、`knowledge`（JSONB：businessMeaning/keyFields/relations/promptHint）、审计字段；唯一键 `(config_id, schema, table)`。
- 备选：继续用 JSON 文件 + 运行时读文件——被否决，入库更可查询、可随部署迁移、可被 Skill 关联与契约导出。

### 2. Skill↔数据库：`business_scene_skill` 增加可空列 `database_config_id`
- 至多关联一个连接实例（用户已确认 `limit_one`），用单列即可，避免多对多关系表。
- `BusinessSceneSkill` 增加 `databaseConfigId`；`BusinessSceneSkillContext` 增加 `databaseConfigId`（可空）送入模型。

### 3. 表知识注入：选表 + 生成 SQL 两阶段都注入
- `keyFields` 的字段语义（如 `EMP_IDS` 存 USERCODE）在生成 SQL 阶段必需，故在 `selectRelevantTables` 与 `planEmsDatabaseQuery` 两个阶段的输入中都注入该库的表知识摘要（候选表 + promptHint）。
- 注入以「辅助定位参考」口吻呈现；用户 Skill 说明中的明确表名/指令优先，表知识不覆盖用户显式意图；用户已点名表时选表阶段收敛为这些表。
- `skill.description` 回归纯业务说明，不再承载表知识。

### 4. 查询执行：按 Skill 关联的单个数据库
- `EmsDatabaseAssistanceService.collect` 改用任务关联 Skill 的 `databaseConfigId`（不再 `requireEnabled()`）；无关联库时跳过 EMS 查询，降级纯文本。
- 表目录无需多库打标（单库），`EmsDatabaseTableMetadata` 无需加 config_id。

### 5. 业务处理 Skill 必选 + 历史兼容
- 新建任务 `businessSceneSkillId` 必填；未选拒绝创建。
- 历史无 Skill 任务在继续模型分析时降级为不查库的纯文本分析（兼容），关联的 Skill 已删除/失效仍拒绝。

### 6. enabled 与 Skill 关联正交
- `ems_database_connection_config.enabled` 继续表示「全局默认/数据探索」用的连接；Skill 直接按 `config_id` 引用，不要求该连接 enabled。

## Risks / Trade-offs

- [生成 SQL 阶段注入会放大提示词] → 只注入该库粗筛后候选表的 promptHint 摘要，不注入全量字段。
- [表知识 JSON 导入需定位 config_id] → 前置：先在「EMS 数据库配置」页建「隆地-报账claim-UAT」实例，导入脚本按 `name` 查其 config_id 后写入。
- [Skill 必选破坏既有草稿] → 新建任务拒绝未选 Skill；历史任务走纯文本降级，不静默查错库。

## Migration Plan

1. Flyway 新增 `governance.ems_table_knowledge` 表 + `task.business_scene_skill` 加 `database_config_id` 列（幂等迁移）。
2. 先在连接配置页建「隆地」实例，导入脚本按 name 定位 config_id，将 3 个 JSON 写入 `ems_table_knowledge`。
3. 后端接口与模型链路改造 + 前端 Skill 编辑页/工作台改造。
4. 回滚：删除新表与新列 + 回滚代码；`description` 未再写入表知识，可无损回退。
