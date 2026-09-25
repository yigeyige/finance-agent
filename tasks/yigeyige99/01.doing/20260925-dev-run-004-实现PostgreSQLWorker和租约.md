---
id: 20260925-dev-run-004
title: "实现 PostgreSQL Worker 和租约"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-RUN-004
---

# 实现 PostgreSQL Worker 和租约

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-RUN-004`，用于实现 PostgreSQL Worker 和租约。

## 前置条件

- 依赖：[DEV-RUN-001](20260925-dev-run-001-实现权威任务状态机.md)、[DEV-DATA-009](20260925-dev-data-009-实现PostgreSQL仓储事务边界.md)。
- 来源基线：[03-P0任务状态机与运行时.md](../solutions/开发任务基线/03-P0任务状态机与运行时.md)

## 目标

- [ ] 使用 `FOR UPDATE SKIP LOCKED` 领取就绪步骤。
- [ ] 建立租约、心跳、过期接管和优雅停止。
- [ ] API/Worker 使用隔离线程池/连接池。
- [ ] 等待人工任务不持有租约。
- [ ] 交付并核对以下产物：Worker、调度器、租约服务、配置项。

## 验收标准

- 一个任务最多一个活动租约。
- 停止领取后运行步骤在安全点结束。
- 等待任务不消耗 Worker。
- 负向检查：双 Worker、心跳丢失、过期接管和时钟偏差场景有确定行为。
- 实现方检查：编译和租约 SQL 契约；并发运行由用户测试。
- 人工验证：用户执行双实例/双 Worker 测试（可在同一 JAR 内配置多个 Worker）。
- 完成证据：租约记录、接管结果、线程/池配置。

## 来源与关联

- 来源任务：`DEV-RUN-004`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-RUN-004` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
