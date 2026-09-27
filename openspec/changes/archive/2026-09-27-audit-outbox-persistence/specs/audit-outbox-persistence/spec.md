## ADDED Requirements

### Requirement: Append-only chained audit events

系统 SHALL 以规范化 JSON payload 保存只追加的审计事件，并为 payload 和事件整体生成可重算的 SHA-256 摘要；同一 task 的事件通过 `prev_event_digest` 形成摘要链。

#### Scenario: Equivalent JSON produces stable payload digest

- **WHEN** 使用字段顺序不同但语义相同的 JSON 创建审计事件
- **THEN** 规范化后的 payload digest 一致

#### Scenario: Tampered payload is detected

- **WHEN** 读取审计事件后 payload 或摘要被修改
- **THEN** 完整性校验失败并拒绝将其作为可信审计事件返回

#### Scenario: Audit events cannot be updated or deleted

- **WHEN** 应用或数据库客户端尝试更新或删除既有审计事件
- **THEN** 操作失败，历史事件保持不变

### Requirement: Idempotent local outbox

系统 SHALL 以唯一 `idempotency_key` 保存本地 outbox 事件；重复幂等键不得创建第二条事件，并支持查询待投递事件及记录投递尝试结果。

#### Scenario: Duplicate idempotency key returns existing event

- **WHEN** 同一幂等键重复写入 outbox
- **THEN** 仓储返回既有事件且不新增记录

#### Scenario: Pending events are claimable by bounded batch

- **WHEN** 查询待投递 outbox
- **THEN** 只返回未投递且投递次数未超过上限的事件，并按创建时间稳定排序

### Requirement: Atomic audit and outbox transaction

系统 SHALL 在同一本地数据库事务中写入业务调用所需的审计事件和 outbox 事件；任一写入失败时整个事务 SHALL 回滚。

#### Scenario: Outbox failure rolls back audit insert

- **WHEN** 审计写入成功但 outbox 写入失败
- **THEN** 事务整体回滚，数据库中不保留本次新增审计事件

#### Scenario: Successful transaction stores both records

- **WHEN** 审计和 outbox 写入均成功
- **THEN** 两条记录在同一事务提交后可被查询

### Requirement: Audit and outbox data minimization

系统 SHALL 只保存事件类型、聚合标识、操作者、版本、摘要和最小规范化 payload，不复制证据原文、源系统原始响应、凭证或模型 reasoning 内容。

#### Scenario: Forbidden raw content is rejected

- **WHEN** 审计或 outbox payload 包含原始响应、凭证、Token 或 `reasoning_content` 字段
- **THEN** 序列化边界拒绝写入
