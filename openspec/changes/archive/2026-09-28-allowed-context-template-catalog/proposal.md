# Proposal: 实现 AllowedContext 和提示模板版本

## Why

虽然 ModelGateway 已有基础 `AllowedContext` 和模板版本类型，但还缺少从当前任务裁剪证据构造上下文的入口，以及与已发布 runtime bundle 对齐的模板目录。没有这层约束，跨任务证据、历史值、未发布知识或原始 prompt 可能进入模型。

## What Changes

- 增加 `ContextEvidence` 和 `AllowedContextFactory`，只合并当前 task 的已裁剪证据、确认字段和任务元数据，拒绝跨任务、敏感字段和冲突值。
- 增加不保存模板原文的提示模板目录条目、发布状态和 runtime bundle 绑定校验。
- 暴露字段清单摘要，供 ModelGateway 元数据追踪；不保存完整 prompt。

## Scope

本变更不引入 RAG/记忆作为当前事实，不修改 runtime bundle 数据库结构，也不增加第三方依赖。
