---
id: 20260925-dev-har-003
title: "实现 Session、Plan 与业务任务映射"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-HAR-003
---

# 实现 Session、Plan 与业务任务映射

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-HAR-003`，用于实现 Session、Plan 与业务任务映射。

## 前置条件

- 依赖：[DEV-HAR-002](20260925-dev-har-002-完成HarnessAgent最小冒烟.md)、[DEV-RUN-002](20260925-dev-run-002-实现任务发起、前提和范围确认.md)、[DEV-DATA-006](20260925-dev-data-006-建立Harness状态与checkpoint模型.md)。
- 来源基线：[05-P0-Harness与模型.md](../solutions/开发任务基线/05-P0-Harness与模型.md)

## 目标

- [ ] session 绑定 task/user/runtime bundle/scope。
- [ ] 将 Harness plan 映射为计划修订。
- [ ] 只把当前逻辑步骤转成审批卡。
- [ ] 一个推荐、最多两个备选。
- [ ] 交付并核对以下产物：Session 服务、Plan 映射器、计划 API 和 schema。

## 验收标准

- Plan 改写不自动执行。
- 范围变化创建新 scope revision。
- 未批准未来步骤不能预取数据。
- 负向检查：跨任务 session、三项以上备选、计划直接推进状态失败。
- 实现方检查：映射契约检查。
- 人工验证：用户查看计划和审批卡。
- 完成证据：session/plan/task 对照样例。

## 来源与关联

- 来源任务：`DEV-HAR-003`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-HAR-003` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
