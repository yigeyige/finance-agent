---
id: 20260925-dev-qa-008
title: "用户手动验证真实 DeepSeek"
status: todo
priority: medium
owner: ""
created_at: 2026-09-25
type: test
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-QA-008
---

# 用户手动验证真实 DeepSeek

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-QA-008`，用于用户手动验证真实 DeepSeek。

## 前置条件

- 依赖：[DEV-HAR-007](20260925-dev-har-007-实现DeepSeekOpenAI兼容适配器.md)、[DEV-HAR-008](20260925-dev-har-008-控制Thinking内容生命周期.md)、[DEV-QA-002](20260925-dev-qa-002-贯通核心合成纵向切片.md)。
- 来源基线：[07-P0测试与验收.md](../solutions/开发任务基线/07-P0测试与验收.md)

## 目标

- [ ] 提供单独命令和最小合成任务。
- [ ] 默认测试禁用真实网络。
- [ ] 不要求实现方持有 API Key。
- [ ] 交付并核对以下产物：live-test 指南和结果记录模板。

## 验收标准

- `deepseek-flash` 完成结构化步骤/工具循环。
- 记录响应模型、token、schema 和完成原因。
- 不记录 prompt/response/reasoning。
- 实现方检查：HTTP Stub 契约。
- 人工验证：用户设置 `DEEPSEEK_API_KEY` 手动执行。
- 完成证据：用户填写的非敏感结果。

## 来源与关联

- 来源任务：`DEV-QA-008`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-QA-008` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
