# postgres-harness-state Specification

## Purpose
TBD - created by archiving change postgres-harness-state-adapter. Update Purpose after archive.
## Requirements
### Requirement: PostgreSQL AgentStateStore boundary

系统 SHALL 通过现有 Harness session PostgreSQL 仓储适配 AgentScope `AgentStateStore`，按 user/session 定位并关联业务 task；业务 task 状态仍是权威。

#### Scenario: Bound state survives process restart
- **WHEN** 已绑定 user/session 的 AgentScope 状态被保存后再次通过适配器读取
- **THEN** 从 `session_state` 恢复同一结构化状态，不创建匿名 session

#### Scenario: Cross-user or unbound recovery fails closed
- **WHEN** 使用其他 user、未绑定 session 或 agentId/userId 不一致读取
- **THEN** 返回稳定失败，不读取其他任务状态

### Requirement: Versioned and digest-checked state

系统 SHALL 在保存状态时使用版本化 Jackson JSON 和 SHA-256 摘要，在恢复时拒绝损坏摘要、未知 schema 和旧 runtime/harness 版本。

#### Scenario: Corrupt state is rejected
- **WHEN** 持久化 JSON 摘要不匹配或 schema 未知
- **THEN** 恢复失败，不把损坏内容交给 Harness

#### Scenario: Stale runtime state is rejected
- **WHEN** session 的 harness/runtime 版本与当前适配器不一致
- **THEN** 返回旧版本失败，不以旧状态继续运行

### Requirement: Sensitive state boundary

系统 SHALL 复用项目状态引用和敏感字段校验，禁止原始响应、凭据、token 和 reasoning 进入持久化状态。

#### Scenario: Forbidden state field is rejected
- **WHEN** AgentScope state 序列化结果包含禁止字段
- **THEN** 保存失败且不更新 PostgreSQL session 状态

