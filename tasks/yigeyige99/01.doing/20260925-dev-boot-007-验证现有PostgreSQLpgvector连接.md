---
id: 20260925-dev-boot-007
title: "验证现有 PostgreSQL/pgvector 连接"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-BOOT-007
---

# 验证现有 PostgreSQL/pgvector 连接

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-BOOT-007`，用于验证现有 PostgreSQL/pgvector 连接。 方案追踪：主方案第 11、12 节。

## 前置条件

- 依赖：[DEV-BOOT-004](20260925-dev-boot-004-建立配置profile与启动前校验.md)。
- 来源基线：[01-P0工程与技术验证.md](../solutions/开发任务基线/01-P0工程与技术验证.md)

## 目标

- [ ] 使用环境变量连接本机 `postgres-pgvector`。
- [ ] 确认 PostgreSQL 17.11 和 pgvector 0.8.6。
- [ ] 规划 `finance_agent` 数据库和迁移/应用角色。
- [ ] 提供连接探针，不在代码中保存管理员凭证。
- [ ] 交付并核对以下产物：连接配置样例、版本探针、数据库准备说明。

## 验收标准

- 连接失败、版本不符、扩展缺失均有明确状态。
- 应用启动不自动创建数据库管理员对象。
- 负向检查：无凭证时不能回退到默认密码。
- 实现方检查：编译与探针契约；实际连接仅在用户准备环境后执行。
- 人工验证：用户提供环境变量并执行连接探针。
- 完成证据：用户的版本/连接结果，不含凭证。

## 来源与关联

- 来源任务：`DEV-BOOT-007`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-BOOT-007` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
