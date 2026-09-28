---
id: 20260925-dev-har-013
title: "实现受控 Long-term Memory"
status: done
priority: medium
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: controlled-long-term-memory
source_task: DEV-HAR-013
---

# 实现受控 Long-term Memory

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-HAR-013`，用于实现受控 Long-term Memory。

## 前置条件

- 依赖：[DEV-HAR-005](20260925-dev-har-005-实现PostgreSQLHarness状态适配.md)。
- 来源基线：[05-P0-Harness与模型.md](../solutions/开发任务基线/05-P0-Harness与模型.md)

## 目标

- [x] 只接受人工批准的非敏感经验摘要。
- [x] 保存来源任务类别、适用范围、审批和撤回。
- [x] 记忆只影响规划提示，不作为证据。
- [x] 交付并核对以下产物：Memory gateway、策略、审批/撤回接口和合成样例。

## 验收标准

- 配置值、人员编号、任务结论、凭证和业务决定不能写入。
- 负向检查：敏感条目、跨范围检索、撤回条目和 prompt 注入。
- 实现方检查：记忆策略契约。
- 人工验证：用户审批、检索、撤回样例。
- 完成证据：条目和负测记录。

## 来源与关联

- 来源任务：`DEV-HAR-013`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-HAR-013` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
- 2026-09-29：完成长期记忆生命周期、摘要摘要/审批事件、非敏感与 Prompt 注入策略、精确任务类别/适用范围检索、审批/撤回网关和仅规划提示结果；新增合成样例，不作为证据或任务状态输入。
- 2026-09-29：新增敏感内容、人员编号、业务结论、Prompt 注入、审批、撤回、跨范围和 planning-only 契约测试；定向测试通过。
