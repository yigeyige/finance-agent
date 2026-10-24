# Proposal

## Why

`DeepSeekBusinessModelClient` 的 instruction 提示词（角色定义、可访问 schema 白名单、SQL 生成规则、输出 JSON 格式）以及 EMS 表定位/字段语义（`t_audit_configuration.EMP_IDS` 存 USERCODE 等业务知识）全部硬编码在 Java 代码里，既不可配置，也混同了「业务知识」与「技术契约」。用户要求把业务知识放进业务场景 Skill description，把系统提示词放进可配置的提示词模板。

## What Changes

- 修复 `databaseQueryInput`：将 `sceneSkill` 完整投影（name、description、handlingType、premiseType）注入查询规划输入，使 Skill 的固定提示词在选表/生成 SQL 阶段生效。
- 将 EMS 表定位、字段语义与查询顺序知识从 instruction 移出，写入两个业务场景 Skill 的 `description`（提供 SQL 脚本 `scripts/update-business-scene-skill-knowledge.sql`）。
- 新增 `governance.prompt_template` 表与 5 条模型用例模板（选表、查询规划、业务处理初稿、调查方案、证据分析），`DeepSeekBusinessModelClient` 改为从模板读取 instruction 并拼接运行时 input。
- 安全边界不变：`EmsReadOnlySqlGuard` 仍硬编码 schema 白名单 + 只读校验，instruction 中的白名单为软提示。

## Capabilities

### Modified Capabilities

- `business-handling-guidance`: 查询规划阶段注入完整 Skill 上下文，instruction 由可配置模板提供，业务知识由 Skill description 提供。
- `ems-database-connection-analysis`: 查询规划提示词从模板读取，业务知识不再散落于代码。

## Impact

- 后端：`DeepSeekBusinessModelClient`、`DatabaseConfiguredBusinessModelClient`、`BusinessModelConfiguration`、新增 `PromptTemplate` 领域/仓储/实体/Mapper、`V19__prompt_template.sql` 迁移、`BusinessModelClientTest` 测试适配。
- 数据：`task.business_scene_skill.description`（两个 Skill）、`governance.prompt_template`（5 条模板）。
- 脚本：`scripts/update-business-scene-skill-knowledge.sql`。
- 不改变接口契约；安全由 `EmsReadOnlySqlGuard` 保持代码强制。
