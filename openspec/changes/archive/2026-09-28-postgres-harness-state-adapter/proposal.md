# Proposal: 实现 PostgreSQL Harness 状态适配

## Why

AgentScope `AgentStateStore` 只提供 user/session 维度，而业务状态还需要 task 绑定、schema 版本、摘要校验和禁止字段边界。项目已有 Harness session 表和 MyBatis-Plus 仓储，需要把两者连接起来，支持进程重启后的安全恢复。

## What Changes

- 扩展 session 仓储按 user/session 定位绑定任务、列出和删除状态。
- 增加 Jackson AgentState codec 与 PostgreSQL `AgentStateStore` 适配器，按 user/session/task 读写，校验 schema、SHA-256 摘要和 harness/runtime 版本。
- 增加损坏摘要、跨用户和旧 runtime 恢复失败关闭的契约测试；不改变业务 task 的权威性。

## Scope

不在本任务中启动本地服务或执行实际数据库重启验证；生产进程重启由用户手工验证。适配器复用现有 session_state 表，不引入新的第三方依赖或重复状态表。
