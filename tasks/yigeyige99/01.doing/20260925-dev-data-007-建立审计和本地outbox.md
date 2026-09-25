---
id: 20260925-dev-data-007
title: "建立审计和本地 outbox"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-DATA-007
---

# 建立审计和本地 outbox

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-DATA-007`，用于建立审计和本地 outbox。

## 前置条件

- 依赖：[DEV-DATA-002](20260925-dev-data-002-建立身份和运行版本模型.md)、[DEV-DATA-003](20260925-dev-data-003-建立任务、范围、前提和步骤模型.md)。
- 来源基线：[02-P0领域模型与数据库.md](../solutions/开发任务基线/02-P0领域模型与数据库.md)

## 目标

- [ ] 实现 `audit_event` 和 `outbox_event`。
- [ ] 审计事件使用前序摘要链和规范化 payload。
- [ ] 只记录必要元数据，不复制证据原文。
- [ ] 发布事务可同库写入审计和 outbox。
- [ ] 交付并核对以下产物：迁移、审计追加接口、outbox 仓储。

## 验收标准

- 审计只追加。
- 事件摘要可重算。
- 失败能使业务事务整体回滚。
- 负向检查：审计插入失败、重复幂等键和篡改 payload 被发现。
- 实现方检查：事务和摘要契约检查。
- 人工验证：用户执行事务回滚用例。
- 完成证据：摘要向量、回滚结果。

## 来源与关联

- 来源任务：`DEV-DATA-007`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-DATA-007` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
