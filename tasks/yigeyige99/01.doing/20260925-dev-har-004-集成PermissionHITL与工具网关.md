---
id: 20260925-dev-har-004
title: "集成 Permission/HITL 与工具网关"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-HAR-004
---

# 集成 Permission/HITL 与工具网关

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-HAR-004`，用于集成 Permission/HITL 与工具网关。

## 前置条件

- 依赖：[DEV-HAR-003](20260925-dev-har-003-实现Session、Plan与业务任务映射.md)、[DEV-TOOL-002](20260925-dev-tool-002-实现双层工具调用守卫.md)。
- 来源基线：[05-P0-Harness与模型.md](../solutions/开发任务基线/05-P0-Harness与模型.md)

## 目标

- [ ] Harness permission 在工具请求处暂停。
- [ ] 业务层生成审批卡。
- [ ] 批准后仍经业务 ToolGateway 二次守卫。
- [ ] 拒绝/过期返回结构化事件。
- [ ] 交付并核对以下产物：permission middleware、HITL bridge、工具适配器。

## 验收标准

- Harness 许可不能绕过业务审批。
- 恢复后审批仍绑定准确参数。
- 负向检查：prompt 注入要求跳过审批、伪造 permit、运行时动态工具均失败。
- 实现方检查：HITL/工具桥契约检查。
- 人工验证：用户手工批准、拒绝和超时。
- 完成证据：事件、审计和工具调用对照。

## 来源与关联

- 来源任务：`DEV-HAR-004`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-HAR-004` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
