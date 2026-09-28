# Harness State and Checkpoint Model

## Purpose

为 Harness 执行过程提供按 task、user、session 隔离的状态、checkpoint、计划修订和 workspace 引用模型，并通过版本化摘要与严格序列化边界支持安全恢复。
## Requirements
### Requirement: Isolated Harness session state

系统 SHALL 保存与 task、user 和 session 显式关联的 Harness 状态；查询必须同时校验这些边界，并保存 harness version、schema version、状态摘要和最小 JSON 引用。

#### Scenario: Cross-task or cross-user session lookup fails

- **WHEN** 使用不匹配的 task 或 user 查询 session
- **THEN** 仓储不返回该 session

#### Scenario: Session state requires versioned digest

- **WHEN** 创建或更新 session state
- **THEN** schema version、harness version、合法 JSON 和 64 位摘要均不能为空

### Requirement: Safe checkpoint and resume binding

系统 SHALL 只在审批完成、工具调用意图入库、结果裁剪完成、确定性计算完成、草稿生成和发布复核六类安全点追加保存 checkpoint。Checkpoint SHALL 保存 task、user、session、step、scope、approval 和创建时 `runtimeBundle` 绑定；状态不得包含源系统原响应、凭证、Token 或 `reasoning_content`。

#### Scenario: Safe points are the only checkpoint types

- **WHEN** 应用尝试记录非白名单安全点或模型流中间状态
- **THEN** 服务拒绝写入 checkpoint

#### Scenario: Checkpoint records runtime binding

- **WHEN** 在合法安全点创建 checkpoint
- **THEN** 记录包含当前任务的 runtime bundle、完整业务边界、状态 schema 和摘要

#### Scenario: Forbidden state fields are rejected

- **WHEN** checkpoint JSON 包含原始响应、凭证、Token 或 `reasoning_content` 字段
- **THEN** 状态序列化边界拒绝该 checkpoint

#### Scenario: Old scope, approval or runtime checkpoint cannot resume

- **WHEN** checkpoint 的 scope、step、approval 或 runtime bundle 与当前权威上下文不一致，或历史 checkpoint 缺少 runtime bundle
- **THEN** 恢复返回 `NEW_APPROVAL_REQUIRED`，不得跳过人工审批

### Requirement: Checkpoint recovery decision

系统 SHALL 提供恢复决策器，在返回继续执行前重新校验当前 task、user、session、step、scope、approval、runtime bundle、租约和工具调用幂等状态，并只返回 `RESUME`、`REPLAY_RESULT`、`NEW_APPROVAL_REQUIRED` 或 `MANUAL_RECONCILIATION`。

#### Scenario: Compatible checkpoint resumes

- **WHEN** 所有绑定仍有效、没有其他未过期活动租约，且 checkpoint 没有未决副作用
- **THEN** 恢复返回 `RESUME`，不自动跳过人审或执行外部调用

#### Scenario: Persisted result is replayed

- **WHEN** checkpoint 关联的工具调用状态为 `SUCCEEDED` 或 `PARTIAL`
- **THEN** 恢复返回 `REPLAY_RESULT` 和原持久化调用引用，不重新查询源系统

#### Scenario: Unknown or concurrent execution requires reconciliation

- **WHEN** 工具调用状态为 `UNKNOWN`/`RUNNING`，或另一个 Worker 持有未过期租约
- **THEN** 恢复返回 `MANUAL_RECONCILIATION`，不得盲目重试或并行执行

#### Scenario: Definite failure can resume through guards

- **WHEN** 工具调用明确为 `FAILED`，且当前审批和调用额度仍有效
- **THEN** 恢复返回 `RESUME`，后续执行必须重新经过现有守卫并使用新的调用序号

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

