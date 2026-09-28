# Proposal: 完成 HarnessAgent 最小冒烟

## Why

AgentScope Java 2.0.3 的 HarnessAgent 已锁定 API，但项目还缺少可重复的本地构建入口和最小会话冒烟证据。需要在不访问网络、不代表生产持久化完成的前提下验证身份、workspace、Stub 模型和连续调用。

## What Changes

- 增加显式 `userId`/`sessionId` 身份契约和 HarnessAgent factory。
- 使用 AgentScope 2.0.3 的 `HarnessAgent`、`RuntimeContext`、本地 workspace、`InMemoryAgentStateStore` 和本地 Stub 模型。
- 提供两次连续调用的最小冒烟契约测试，记录会话继续、事件流完成及 workspace 边界。

## Scope

本变更只验证本地 Stub 与内存状态存储的兼容性，不承诺生产持久化、真实模型接入或跨进程恢复。
