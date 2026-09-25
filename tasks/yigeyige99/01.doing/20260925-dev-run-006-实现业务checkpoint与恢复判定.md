---
id: 20260925-dev-run-006
title: "实现业务 checkpoint 与恢复判定"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-RUN-006
---

# 实现业务 checkpoint 与恢复判定

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-RUN-006`，用于实现业务 checkpoint 与恢复判定。

## 前置条件

- 依赖：[DEV-RUN-003](20260925-dev-run-003-实现步骤提议和审批卡.md)、[DEV-RUN-005](20260925-dev-run-005-实现幂等和调用意图.md)、[DEV-DATA-006](20260925-dev-data-006-建立Harness状态与checkpoint模型.md)。
- 来源基线：[03-P0任务状态机与运行时.md](../solutions/开发任务基线/03-P0任务状态机与运行时.md)

## 目标

- [ ] 在审批后、调用意图后、裁剪结果后、确定性计算后、草稿后和发布复核后创建检查点。
- [ ] 恢复时重验当前步骤、审批、范围、版本、租约和幂等状态。
- [ ] 交付并核对以下产物：checkpoint 服务、恢复决策器、恢复 API/Worker 路径。

## 验收标准

- 返回 `RESUME`、`REPLAY_RESULT`、`NEW_APPROVAL_REQUIRED` 或 `MANUAL_RECONCILIATION`。
- 不得跳过人审。
- 负向检查：旧 approval、旧 runtime bundle、旧 scope 和未知调用状态不能自动恢复。
- 实现方检查：恢复决策表契约检查。
- 人工验证：用户在多个安全点终止进程并恢复。
- 完成证据：恢复矩阵和用户故障注入结果。

## 来源与关联

- 来源任务：`DEV-RUN-006`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-RUN-006` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
