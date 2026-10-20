# Proposal

## Why

当前"开始模型分析"只基于业务标题/说明生成泛化的业务草稿，未使用已配置的 EMS 数据库真实数据，缺少"需要检查的配置项"与"关联证据"。用户期望系统受控查询 EMS 相关表，产出可核验的「检查结论 + 关联证据」。

## What Changes

- EMS 数据辅助从"单条查询"升级为"表目录粗筛 + 多轮只读查询（最多 3 轮）"，模型可先选表、再生成单条 SELECT/JOIN、执行后按需继续追问。
- 查询证据增加 `schema`、`table` 溯源字段，形成结构化证据 `[{schema, table, purpose, columns, rows}]`。
- 业务处理草稿（draft）新增 `conclusion`（检查结论）与 `evidence`（关联证据）字段，模型基于累积证据生成结论。
- 前端业务处理向导新增"检查结论"与"关联证据表"两个只读展示区。

## Capabilities

### Modified Capabilities

- `business-handling-guidance`: 业务处理草稿增加 `conclusion` 与 `evidence` 字段，模型基于受控 EMS 证据生成结论与关联证据。
- `ems-database-connection-analysis`: 受控模型数据辅助由单条查询改为表目录粗筛 + 多轮只读查询，证据结果带表溯源。

## Impact

- 后端：`EmsDatabaseAssistanceService`（多轮编排）、`DeepSeekBusinessModelClient`（查询规划/方案生成提示词）、`EmsDatabaseEvidence`/`EmsDatabaseQueryRequest`（结构扩展）、`BusinessHandlingDraft`（字段扩展与校验）、`JdbcEmsDatabaseQueryExecutor`（结果溯源）。
- 前端：`WorkbenchHome.vue`（结论与证据展示区）、前端类型定义（`BusinessGuidanceResponse`/draft）。
- 不引入新依赖；SQL 守卫仍限定 8 个业务库单条 SELECT（含多表 JOIN）。
