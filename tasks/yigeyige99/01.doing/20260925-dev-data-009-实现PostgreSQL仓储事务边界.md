---
id: 20260925-dev-data-009
title: "实现 PostgreSQL 仓储事务边界"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-DATA-009
---

# 实现 PostgreSQL 仓储事务边界

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-DATA-009`，用于实现 PostgreSQL 仓储事务边界。

## 前置条件

- 依赖：[DEV-DATA-002](20260925-dev-data-002-建立身份和运行版本模型.md)、[DEV-DATA-003](20260925-dev-data-003-建立任务、范围、前提和步骤模型.md)、[DEV-DATA-004](20260925-dev-data-004-建立证据、覆盖度和判断模型.md)、[DEV-DATA-005](20260925-dev-data-005-建立决策包、复核、发布和交接模型.md)、[DEV-DATA-006](20260925-dev-data-006-建立Harness状态与checkpoint模型.md)、[DEV-DATA-007](20260925-dev-data-007-建立审计和本地outbox.md)、[DEV-DATA-008](20260925-dev-data-008-实现规范化JSON、UUID、时间和摘要基础库.md)。
- 来源基线：[02-P0领域模型与数据库.md](../solutions/开发任务基线/02-P0领域模型与数据库.md)

## 目标

- [ ] 使用 Spring JDBC/显式 SQL 实现仓储。
- [ ] 定义任务锁、发布事务、租约领取和只读查询事务。
- [ ] 禁止 controller/Harness 直接访问 JDBC。
- [ ] 交付并核对以下产物：仓储适配器、事务服务、数据库异常到领域错误映射。

## 验收标准

- 关键事务与附件 A 伪代码一致。
- 锁和唯一约束承担最终并发防线。
- 负向检查：并发更新、死锁重试边界和事务中途失败不产生半成品。
- 实现方检查：编译和 SQL 契约；并发数据库测试由用户执行。
- 人工验证：用户执行并发与回滚测试。
- 完成证据：事务清单、用户结果、提交号。

## 来源与关联

- 来源任务：`DEV-DATA-009`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-DATA-009` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
