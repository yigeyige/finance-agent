# Design

## Context

- 现有能力：`EmsDatabaseAssistanceService.collect` 单次调用模型规划一条 SQL，经 `EmsReadOnlySqlGuard` 执行后把结果作为 `EmsDatabaseEvidence` 注入 `guideBusinessHandling`。
- 现状问题：模型常返回 `needQuery=false` 或查询结果未进入最终草稿，方案缺少真实数据支撑。
- 约束：EMS 只读访问限 8 个业务库、单条 SELECT（含多表 JOIN）；`listAllowedTables`/`listColumns` 已提供元数据目录；`BusinessHandlingDraft` 目前是固定 14 字段。

## Goals / Non-Goals

**Goals:**
- 让模型先看粗筛后的表目录选表，再执行多轮只读查询，累积带溯源的证据。
- 让 `draft` 输出「检查结论 + 关联证据」，前端可见。

**Non-Goals:**
- 不把证据持久化到 evidence 权威表（仍为本次方案生成的临时上下文）。
- 不放开 SQL 守卫（仍限 8 库单条 SELECT/JOIN）。
- 不引入新依赖、不改数据库 schema。

## Decisions

### D1: 表目录粗筛关键词
`listAllowedTables` 返回全量表（约 650 张），直接喂模型会超上下文。粗筛关键词采用：`approve/audit/role/user/org/process/flow/config/employee/staff/person/permission/authorize/esb/category/claim`，按表名或表注释匹配，得到候选表（含 `schema.table` + 注释）再给模型。

### D2: 多轮查询编排（最多 3 轮）
`EmsDatabaseAssistanceService` 改为循环：
1. 粗筛表目录；
2. 每轮调用 `planEmsDatabaseQuery`（输入：业务说明 + 候选表目录 + 已累积证据摘要）；
3. `needQuery=false` 或轮次达 3 即停止；
4. 每轮通过 `sqlGuard.validate` 后 `executor.execute`，结果累积为 `EmsDatabaseEvidence` 列表。

### D3: 证据溯源
`EmsDatabaseEvidence` 增加 `schema`、`table`。溯源取自模型 SQL 的 `FROM` 子句第一个 `schema.table`（正则提取，复用守卫已有 `FROM_OR_JOIN` 思路），避免扩展模型输出契约、降低解析失败面。

### D4: 证据摘要喂给模型
每轮把「已累积证据」喂回模型时，`columns` 全量，`rows` 只取前 20 行，防止多轮后上下文膨胀。

### D5: draft 字段扩展
`BusinessHandlingDraft` 增加：
- `conclusion`（检查结论，文本，≤2000）；
- `evidence`（结构化数组 `[{schema, table, purpose, columns[], rows[][]}]`）。
`fromJson` 相应校验；`guideBusinessHandling` 提示词要求输出这两个字段。

### D6: 前端展示
`WorkbenchHome.vue` 在「对象与处理范围」下增加两个只读区：**检查结论**（文本）、**关联证据表**（`el-table` 渲染 `evidence`）。前端类型同步扩展。

## Risks / Trade-offs

- [模型选表不准/生成非法 SQL] → 守卫拒绝后静默降级，仍产出不含证据的方案，不阻塞主流程。
- [多轮查询增加总耗时] → 前端已放宽到 240s、后端单次 180s，轮次上限 3 控制总时长。
- [证据含敏感数据原样展示] → 用户已确认原样返回；后续如需脱敏在字段策略层加白名单。
- [关键词粗筛漏掉相关表] → 关键词覆盖审批/角色/人员/流程/配置主线，后续可按案例沉淀为 skill 再收敛。

## Migration Plan

- 纯代码变更，无数据库迁移。后端重新打包、重启即可；前端 Vite 热更新或刷新生效。
- 回滚：恢复改动前代码并重新打包重启。

## Open Questions

- 后续是否把证据持久化为权威 evidence、并沉淀为业务场景 Skill（用户表示后续总结，暂不实施）。
