---
id: 20260925-dev-har-010
title: "实现受控 Workspace"
status: todo
priority: medium
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-HAR-010
---

# 实现受控 Workspace

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-HAR-010`，用于实现受控 Workspace。

## 前置条件

- 依赖：[DEV-HAR-002](20260925-dev-har-002-完成HarnessAgent最小冒烟.md)、[DEV-BOOT-004](20260925-dev-boot-004-建立配置profile与启动前校验.md)。
- 来源基线：[05-P0-Harness与模型.md](../solutions/开发任务基线/05-P0-Harness与模型.md)

## 目标

- [ ] 按 task/session 隔离目录。
- [ ] 服务端生成路径。
- [ ] 设置配额、允许类型和清理接口。
- [ ] 权威证据不以 workspace 为唯一副本。
- [ ] 交付并核对以下产物：Workspace manager、路径策略和 local/test 配置。

## 验收标准

- ROOTED 路径、跨任务隔离、配额有效。
- prod 不允许未配置本地回退。
- 负向检查：绝对路径、`..`、符号链接逃逸和跨用户访问。
- 实现方检查：路径安全契约。
- 人工验证：用户检查任务目录隔离。
- 完成证据：路径负测结果。

## 来源与关联

- 来源任务：`DEV-HAR-010`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-HAR-010` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
