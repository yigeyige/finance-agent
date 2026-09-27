---
id: 20260925-dev-data-008
title: "实现规范化 JSON、UUID、时间和摘要基础库"
status: done
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: canonical-time-identity-foundation
source_task: DEV-DATA-008
---

# 实现规范化 JSON、UUID、时间和摘要基础库

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-DATA-008`，用于实现规范化 JSON、UUID、时间和摘要基础库。

## 前置条件

- 依赖：[DEV-BOOT-003](20260925-dev-boot-003-创建SpringBoot模块化单体骨架.md)。
- 来源基线：[02-P0领域模型与数据库.md](../solutions/开发任务基线/02-P0领域模型与数据库.md)

## 目标

- [x] 统一应用生成 UUID。
- [x] 数据库 `timestamptz`/UTC。
- [x] API ISO 8601 UTC。
- [x] UI Asia/Shanghai。
- [x] 实现确定性 JSON canonicalization 和 SHA-256。
- [x] 时钟通过 `Clock` 注入。
- [x] 交付并核对以下产物：共享值对象、序列化配置、摘要工具和测试向量。

## 验收标准

- 相同语义输入得到相同摘要。
- 字段顺序和系统默认时区不影响结果。
- 负向检查：浮点/数字、null、Unicode、时区和数组顺序边界有明确规则。
- 实现方检查：固定测试向量契约检查。
- 人工验证：无。
- 完成证据：测试向量和输出摘要。

## 来源与关联

- 来源任务：`DEV-DATA-008`
- OpenSpec 变更：`canonical-time-identity-foundation`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-DATA-008` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
- 2026-09-27：新增共享规范化 JSON、SHA-256、UUID、UTC 时间基础能力，审计摘要复用共享实现；新增 Vue 2/TypeScript 的 Asia/Shanghai 时间展示工具和固定契约测试。
- 2026-09-27：后端编译及全量测试通过；前端 `npm run typecheck`、`npm run build` 通过（仅保留既有 chunk 体积告警）；OpenSpec `canonical-time-identity-foundation` 清单完成，等待用户确认归档。
- 2026-09-27：已同步主规格并归档 OpenSpec `canonical-time-identity-foundation`；任务文件移入 `03.achieved/`。
