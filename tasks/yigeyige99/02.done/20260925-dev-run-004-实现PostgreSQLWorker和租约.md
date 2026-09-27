---
id: 20260925-dev-run-004
title: "实现 PostgreSQL Worker 和租约"
status: done
priority: high
owner: yigeyige99
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: postgres-worker-leases
source_task: DEV-RUN-004
---

# 实现 PostgreSQL Worker 和租约

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-RUN-004`，用于实现 PostgreSQL Worker 和租约。

## 前置条件

- 依赖：[DEV-RUN-001](20260925-dev-run-001-实现权威任务状态机.md)、[DEV-DATA-009](20260925-dev-data-009-实现PostgreSQL仓储事务边界.md)。
- 来源基线：[03-P0任务状态机与运行时.md](../../solutions/开发任务基线/03-P0任务状态机与运行时.md)

## 目标

- [x] 使用 `FOR UPDATE SKIP LOCKED` 领取就绪步骤。
- [x] 建立租约、心跳、过期接管和优雅停止。
- [x] API/Worker 使用隔离线程池/连接池。
- [x] 等待人工任务不持有租约。
- [x] 交付并核对以下产物：Worker、调度器、租约服务、配置项。

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
- OpenSpec 变更：`postgres-worker-leases`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-RUN-004` 拆分为独立 task。
- 2026-09-28：完成 `postgres-worker-leases` OpenSpec 变更；实现原子领取、持有者绑定租约、过期接管、Worker 调度/优雅停止，以及 API/Worker 独立线程池和 Hikari 连接池路由。
- 2026-09-28：完成后端 Maven 测试、编译和 OpenSpec 严格校验；双 Worker、心跳丢失、过期接管和时钟偏差的真实并发验证由用户启动本地环境后执行。
