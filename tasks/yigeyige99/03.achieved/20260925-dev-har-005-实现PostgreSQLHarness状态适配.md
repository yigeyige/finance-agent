---
id: 20260925-dev-har-005
title: "实现 PostgreSQL Harness 状态适配"
status: completed
priority: high
owner: yigeyige99
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: postgres-harness-state-adapter
source_task: DEV-HAR-005
---

# 实现 PostgreSQL Harness 状态适配

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-HAR-005`，用于实现 PostgreSQL Harness 状态适配。

## 前置条件

- 依赖：[DEV-HAR-002](20260925-dev-har-002-完成HarnessAgent最小冒烟.md)、[DEV-DATA-006](20260925-dev-data-006-建立Harness状态与checkpoint模型.md)。
- 来源基线：[05-P0-Harness与模型.md](../solutions/开发任务基线/05-P0-Harness与模型.md)

## 目标

- [x] 先确认 AgentScope 2.0.3 `AgentStateStore`/相关 SPI。
- [x] 优先实现直接 PostgreSQL 适配。
- [x] 若 SPI 不足，使用受支持扩展点和业务侧状态映射，保留差异记录。
- [x] 以 user/session 定位并关联 task。
- [x] 交付并核对以下产物：状态适配器、序列化版本、迁移/仓储、兼容性决策记录。

## 验收标准

- 进程重启后 session 恢复。
- 业务状态仍是权威。
- 状态不含禁止字段和 reasoning。
- 负向检查：摘要损坏、未知 schema、跨用户恢复和旧 runtime bundle 恢复失败关闭。
- 实现方检查：适配器契约和编译；实际重启由用户验证。
- 人工验证：用户停止并重启 JAR 后继续任务。
- 完成证据：PG 状态记录、恢复结果和 SPI 结论。

## 来源与关联

- 来源任务：`DEV-HAR-005`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-HAR-005` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
- 2026-09-28：确认 AgentScope 2.0.3 `AgentStateStore` SPI 后，复用现有 `harness.session_state` MyBatis-Plus 仓储实现 PostgreSQL 适配；新增 Jackson 版本化状态 codec、摘要/runtime 版本失败关闭及契约测试。通过定向测试、`mvn -q verify`、Surefire 汇总检查和 `openspec validate --strict`；进程重启人工验证留给用户。
