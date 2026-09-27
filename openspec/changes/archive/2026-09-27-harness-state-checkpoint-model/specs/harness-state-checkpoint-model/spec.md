## ADDED Requirements

### Requirement: Isolated Harness session state

系统 SHALL 保存与 task、user 和 session 显式关联的 Harness 状态；查询必须同时校验这些边界，并保存 harness version、schema version、状态摘要和最小 JSON 引用。

#### Scenario: Cross-task or cross-user session lookup fails
- **WHEN** 使用不匹配的 task 或 user 查询 session
- **THEN** 仓储不返回该 session

#### Scenario: Session state requires versioned digest
- **WHEN** 创建或更新 session state
- **THEN** schema version、harness version、合法 JSON 和 64 位摘要均不能为空

### Requirement: Safe checkpoint and resume binding

系统 SHALL 保存 checkpoint 的 task、user、session、step、scope 和 approval 绑定；checkpoint 状态不得包含源系统原响应、凭证、Token 或 `reasoning_content`。

#### Scenario: Forbidden state fields are rejected
- **WHEN** checkpoint JSON 包含原始响应、凭证、Token 或 `reasoning_content` 字段
- **THEN** 状态序列化边界拒绝该 checkpoint

#### Scenario: Old scope or approval checkpoint cannot resume
- **WHEN** checkpoint 的 scope、step 或 approval 与当前批准上下文不一致
- **THEN** 恢复校验失败并要求重新确认

### Requirement: Versioned plan and workspace references

系统 SHALL 以追加修订保存 Harness plan，并以受控引用保存 workspace 路径、对象引用和摘要；计划与 workspace 查询必须隔离 task、user 和 session。

#### Scenario: Plan revisions are unique per session
- **WHEN** 同一 session 写入重复 plan revision number
- **THEN** 数据库唯一约束拒绝重复修订

#### Scenario: Workspace reference does not store raw content
- **WHEN** 创建 workspace 引用
- **THEN** 记录只保存相对路径、对象引用、摘要和媒体类型，不保存文件原文

### Requirement: Explicit state serialization contract

系统 SHALL 提供可替换的状态序列化接口，所有状态读写通过带 schema version 和 digest 的 `StateReference`，不得把禁止字段写入 JSONB。

#### Scenario: Serializer round-trips safe state
- **WHEN** 编解码合法最小状态引用
- **THEN** 输出保持 schema version、JSON 和 digest 一致

#### Scenario: Unknown or sensitive state is rejected
- **WHEN** 状态包含未允许的敏感字段
- **THEN** serializer 在持久化前抛出校验异常
