---
id: 20260925-dev-har-007
title: "实现 DeepSeek OpenAI 兼容适配器"
status: completed
priority: high
owner: yigeyige99
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: deepseek-openai-adapter
source_task: DEV-HAR-007
---

# 实现 DeepSeek OpenAI 兼容适配器

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-HAR-007`，用于实现 DeepSeek OpenAI 兼容适配器。

## 前置条件

- 依赖：[DEV-HAR-001](20260925-dev-har-001-实现ModelGateway和确定性Stub.md)、[DEV-BOOT-004](20260925-dev-boot-004-建立配置profile与启动前校验.md)。
- 来源基线：[05-P0-Harness与模型.md](../solutions/开发任务基线/05-P0-Harness与模型.md)

## 目标

- [x] base URL 默认 `https://api.deepseek.com`，model 默认 `deepseek-flash`，Chat Completions，API Key 环境变量。
- [x] 支持 JSON Schema/工具调用、超时、取消、token 和响应模型元数据。
- [x] 不实现自动模型切换。
- [x] 交付并核对以下产物：DeepSeek 配置、模型适配器、手工 live-test profile 和说明。

## 验收标准

- 无 key 时默认测试不调用。
- live profile 明确由用户手动触发。
- 响应结构经 schema 和业务校验。
- 负向检查：key 不进入日志。
- 负向检查：模型错误/限流进入暂停而非切换模型。
- 实现方检查：使用 HTTP Stub 的协议契约；不执行真实调用。
- 人工验证：用户设置 `DEEPSEEK_API_KEY` 并执行真实模型命令。
- 完成证据：Stub 协议结果、用户提供的模型名/token/schema 结果。

## 来源与关联

- 来源任务：`DEV-HAR-007`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-HAR-007` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
- 2026-09-28：完成 DeepSeek OpenAI Chat Completions 适配器、离线 HTTP Stub 契约测试、手工 live-test profile 与说明；通过定向测试、`mvn -q verify`、Surefire 失败扫描和严格 OpenSpec 校验。
