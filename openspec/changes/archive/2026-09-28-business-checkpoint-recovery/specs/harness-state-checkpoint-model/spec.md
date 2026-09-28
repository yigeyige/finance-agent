## MODIFIED Requirements

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
