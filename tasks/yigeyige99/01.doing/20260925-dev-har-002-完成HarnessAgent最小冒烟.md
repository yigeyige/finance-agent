---
id: 20260925-dev-har-002
title: "完成 HarnessAgent 最小冒烟"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-HAR-002
---

# 完成 HarnessAgent 最小冒烟

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-HAR-002`，用于完成 HarnessAgent 最小冒烟。

## 前置条件

- 依赖：[DEV-BOOT-002](20260925-dev-boot-002-锁定Java与AgentScope依赖组合.md)、[DEV-HAR-001](20260925-dev-har-001-实现ModelGateway和确定性Stub.md)。
- 来源基线：[05-P0-Harness与模型.md](../solutions/开发任务基线/05-P0-Harness与模型.md)

## 目标

- [ ] 使用 AgentScope Java 2.0.3 `HarnessAgent`、Stub 模型、本地 workspace 和默认状态存储，验证 session、一次调用、事件流和重连。
- [ ] 记录实际 API 与设计差异。
- [ ] 交付并核对以下产物：Harness factory、冒烟入口、兼容性记录。

## 验收标准

- 同 user/session 能继续会话。
- 不同 user/session 隔离。
- 此任务不代表生产持久化完成。
- 负向检查：缺 userId/sessionId、路径越界和交叉会话读取失败。
- 实现方检查：编译和本地 Stub 冒烟契约。
- 人工验证：用户可手工运行冒烟命令。
- 完成证据：事件序列和兼容性结论。

## 来源与关联

- 来源任务：`DEV-HAR-002`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-HAR-002` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
