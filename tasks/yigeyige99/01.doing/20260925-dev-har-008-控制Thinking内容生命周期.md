---
id: 20260925-dev-har-008
title: "控制 Thinking 内容生命周期"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-HAR-008
---

# 控制 Thinking 内容生命周期

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-HAR-008`，用于控制 Thinking 内容生命周期。

## 前置条件

- 依赖：[DEV-HAR-007](20260925-dev-har-007-实现DeepSeekOpenAI兼容适配器.md)。
- 来源基线：[05-P0-Harness与模型.md](../solutions/开发任务基线/05-P0-Harness与模型.md)

## 目标

- [ ] 默认 thinking high。
- [ ] 工具循环内按 DeepSeek 协议传递 `reasoning_content`。
- [ ] 仅存在于易失内存，循环结束即释放。
- [ ] 事件映射、异常、日志、checkpoint 和数据库均忽略该字段。
- [ ] 交付并核对以下产物：响应过滤器、瞬时上下文容器、禁止字段扫描规则。

## 验收标准

- 工具调用协议正常。
- 重启不依赖 reasoning 恢复。
- 用户只能看到最终结果和简明依据。
- 负向检查：在 reasoning 中注入唯一标记后，全持久层/日志/SSE 扫描零命中。
- 实现方检查：HTTP Stub 协议和过滤契约。
- 人工验证：真实模型场景由用户执行扫描。
- 完成证据：零留存扫描报告。

## 来源与关联

- 来源任务：`DEV-HAR-008`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-HAR-008` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
