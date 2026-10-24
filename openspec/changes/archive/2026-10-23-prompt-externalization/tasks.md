# Tasks

## 1. 业务知识外置到 Skill

- [x] 1.1 `databaseQueryInput` 注入完整 sceneSkill 投影（name/description/handlingType/premiseType）
- [x] 1.2 从 `selectRelevantTables`/`planEmsDatabaseQuery` instruction 移除表定位与字段语义
- [x] 1.3 通过 SQL 更新两个 Skill 的 description，并落盘 `scripts/update-business-scene-skill-knowledge.sql`

## 2. 系统提示词外置到模板表

- [x] 2.1 新增 `V19__prompt_template.sql`（`governance.prompt_template` 表 + 5 条模板）
- [x] 2.2 新增 `PromptTemplate` 领域、`PromptTemplateRepository` 端口、实体/Mapper/MyBatis 实现
- [x] 2.3 `DeepSeekBusinessModelClient` 5 处 instruction 改为从模板读取 + 拼运行时 input
- [x] 2.4 `DatabaseConfiguredBusinessModelClient`/`BusinessModelConfiguration`/`BusinessModelClientTest` 同步适配

## 3. 验证

- [x] 3.1 后端 JDK17 编译（含测试代码）通过
- [x] 3.2 复测 guidance 返回 200，模型纯靠模板 + Skill description 正确查出 ZENGLINGRUI 15 条审批配置
