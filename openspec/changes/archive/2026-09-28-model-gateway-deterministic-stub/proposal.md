# Proposal: 实现 ModelGateway 和确定性 Stub

## Why

Harness 需要统一的结构化模型调用边界，但当前没有固定用途、模板、字段投影、预算、取消和 schema 校验契约。缺少离线可复现的 Stub 会让默认测试意外访问网络或依赖自然语言输出。

## What Changes

- 定义 `ModelGateway` 结构化调用契约及用途、模板、允许上下文、预算、取消、结果和元数据类型。
- 提供严格的步骤提议输出 schema 校验器，拒绝额外字段和非法结构。
- 提供按 fixture 稳定返回结构化结果的确定性 Stub；缺失 fixture、取消、预算超限和 schema 失败均以稳定错误拒绝。
- 记录仅包含版本、摘要、token、延迟和完成原因等模型元数据，不持久化完整 prompt、response 或 reasoning。

## Scope

本变更不接入真实模型网络调用，不实现自动模型切换，也不引入新的第三方依赖。
