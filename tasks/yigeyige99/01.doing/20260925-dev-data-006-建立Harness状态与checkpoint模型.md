---
id: 20260925-dev-data-006
title: "建立 Harness 状态与 checkpoint 模型"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-DATA-006
---

# 建立 Harness 状态与 checkpoint 模型

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-DATA-006`，用于建立 Harness 状态与 checkpoint 模型。

## 前置条件

- 依赖：[DEV-DATA-003](20260925-dev-data-003-建立任务、范围、前提和步骤模型.md)。
- 来源基线：[02-P0领域模型与数据库.md](../solutions/开发任务基线/02-P0领域模型与数据库.md)

## 目标

- [ ] 实现 `harness.session_state`、`harness.checkpoint`、计划修订和 workspace 引用表。
- [ ] 业务 task/user/session 显式关联。
- [ ] 状态内容使用 schema 版本、摘要和最小恢复引用。
- [ ] 交付并核对以下产物：迁移、仓储、状态序列化接口。

## 验收标准

- 跨任务/跨用户 session 查询失败。
- checkpoint 不能包含源原响应、凭证或 reasoning_content。
- 负向检查：旧 scope/approval checkpoint 恢复校验失败。
- 实现方检查：序列化和字段白名单契约检查。
- 人工验证：用户抽查 checkpoint 内容。
- 完成证据：schema、样例 checkpoint 和扫描结果。

## 来源与关联

- 来源任务：`DEV-DATA-006`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-DATA-006` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
