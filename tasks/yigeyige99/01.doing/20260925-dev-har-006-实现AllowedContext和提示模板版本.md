---
id: 20260925-dev-har-006
title: "实现 AllowedContext 和提示模板版本"
status: todo
priority: high
owner: ""
created_at: 2026-09-25
type: development
source: development-workplan
openspec_change: finance-agent-development-task-baseline
source_task: DEV-HAR-006
---

# 实现 AllowedContext 和提示模板版本

## 背景

本任务来自财务共享智能运维 Agent 开发任务基线中的 `DEV-HAR-006`，用于实现 AllowedContext 和提示模板版本。

## 前置条件

- 依赖：[DEV-HAR-001](20260925-dev-har-001-实现ModelGateway和确定性Stub.md)、[DEV-TOOL-003](20260925-dev-tool-003-实现三类字段策略与可信裁剪.md)、[DEV-HAR-003](20260925-dev-har-003-实现Session、Plan与业务任务映射.md)。
- 来源基线：[05-P0-Harness与模型.md](../solutions/开发任务基线/05-P0-Harness与模型.md)

## 目标

- [ ] 只从已裁剪证据、任务元数据和允许的确认内容构造上下文。
- [ ] 模板版本化并进入 runtime bundle。
- [ ] 禁止 RAG/记忆作为当前事实。
- [ ] 交付并核对以下产物：上下文构造器、模板目录、字段清单摘要和校验器。

## 验收标准

- 每次模型调用可追到证据 ID、字段清单和模板版本。
- 不保存原 prompt。
- 负向检查：禁止字段、未发布知识、跨任务证据和历史配置值不能进入上下文。
- 实现方检查：上下文字段契约和敏感扫描。
- 人工验证：用户查看元数据而非原 prompt。
- 完成证据：上下文清单和扫描结果。

## 来源与关联

- 来源任务：`DEV-HAR-006`
- OpenSpec 变更：`finance-agent-development-task-baseline`
- 目标代码仓库：`D:\Work\CR_git\emsld\finance_agent`

## 执行记录

- 2026-09-25：从开发任务基线 `DEV-HAR-006` 拆分为独立 task；当前状态为 `todo`，尚未开始开发。
