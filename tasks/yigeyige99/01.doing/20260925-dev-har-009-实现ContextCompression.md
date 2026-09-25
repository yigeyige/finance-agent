---
id: 20260925-dev-har-009
title: "实现 Context Compression"
status: todo
priority: medium
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-HAR-009
---

# 实现 Context Compression

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-HAR-009`，用于实现 Context Compression。

## 前置条件

- 依赖：[DEV-HAR-005](20260925-dev-har-005-实现PostgreSQLHarness状态适配.md)、[DEV-HAR-006](20260925-dev-har-006-实现AllowedContext和提示模板版本.md)。
- 来源基线：[05-P0-Harness与模型.md](../solutions/开发任务基线/05-P0-Harness与模型.md)

## 目标

- [ ] 压缩输出只含任务/范围/前提/证据/finding/current step 引用和摘要。
- [ ] 验证引用存在与 digest 匹配。
- [ ] 压缩摘要不能升级为证据。
- [ ] 交付并核对以下产物：压缩配置、结构化摘要 schema、验证器。

## 验收标准

- 压缩前后关键引用和未知项一致。
- 压缩失败转人工缩小范围。
- 负向检查：伪造 snapshot ID、摘要不匹配和丢失未知项失败。
- 实现方检查：压缩结构契约；模型效果由用户验证。
- 人工验证：用户运行长会话并比较压缩前后。
- 完成证据：对照结果和失败样例。

## 来源与关联

- 来源任务：`DEV-HAR-009`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-HAR-009` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
