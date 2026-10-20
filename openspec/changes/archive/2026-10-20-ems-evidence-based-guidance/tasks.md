# Tasks

## 1. 后端证据与草稿模型扩展

- [x] 1.1 在 `EmsDatabaseEvidence` 增加 `schema`、`table` 字段并补齐校验；验证 `./mvnw -f backend/pom.xml -q -DskipTests package` 编译通过
- [x] 1.2 在 `BusinessHandlingDraft` 增加 `conclusion`、`evidence` 字段及 `fromJson` 校验；验证编译通过

## 2. 多轮查询编排与表目录粗筛

- [x] 2.1 在 `EmsDatabaseAssistanceService` 实现关键词粗筛表目录（基于 `listAllowedTables`）
- [x] 2.2 将 `collect` 改为多轮循环（最多 3 轮），每轮 plan→guard→execute→累积证据
- [x] 2.3 从 SQL 的 `FROM` 子句提取首个 `schema.table` 作为证据溯源

## 3. 模型提示词调整

- [x] 3.1 `planEmsDatabaseQuery` 提示词注入候选表目录与已累积证据摘要（rows 前 20 行）
- [x] 3.2 `guideBusinessHandling` 提示词要求输出 `conclusion` 与结构化 `evidence`

## 4. 前端结论与证据展示

- [x] 4.1 前端类型定义同步扩展 `conclusion`、`evidence`；验证 `npm run typecheck` 通过
- [x] 4.2 `WorkbenchHome.vue` 增加「检查结论」与「关联证据表」两个只读展示区

## 5. 端到端验证

- [x] 5.1 端到端：LILYCHANG / ZENGLINGRUI 两个真实人员案例验证通过，guidance 返回 200，draft 含非空 conclusion，evidence 带 schema/table 溯源与真实行数据

## Workflow follow-up

- 实现并验证通过后，按项目流程归档该 change 并同步 specs。
