---
id: 20260925-dev-data-008
title: "实现规范化 JSON、UUID、时间和摘要基础库"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-DATA-008
---

# 实现规范化 JSON、UUID、时间和摘要基础库

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-DATA-008`，用于实现规范化 JSON、UUID、时间和摘要基础库。

## 前置条件

- 依赖：[DEV-BOOT-003](20260925-dev-boot-003-创建SpringBoot模块化单体骨架.md)。
- 来源基线：[02-P0领域模型与数据库.md](../solutions/开发任务基线/02-P0领域模型与数据库.md)

## 目标

- [ ] 统一应用生成 UUID。
- [ ] 数据库 `timestamptz`/UTC。
- [ ] API ISO 8601 UTC。
- [ ] UI Asia/Shanghai。
- [ ] 实现确定性 JSON canonicalization 和 SHA-256。
- [ ] 时钟通过 `Clock` 注入。
- [ ] 交付并核对以下产物：共享值对象、序列化配置、摘要工具和测试向量。

## 验收标准

- 相同语义输入得到相同摘要。
- 字段顺序和系统默认时区不影响结果。
- 负向检查：浮点/数字、null、Unicode、时区和数组顺序边界有明确规则。
- 实现方检查：固定测试向量契约检查。
- 人工验证：无。
- 完成证据：测试向量和输出摘要。

## 来源与关联

- 来源任务：`DEV-DATA-008`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-DATA-008` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
