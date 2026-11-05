## MODIFIED Requirements

### Requirement: Append-only chained audit events

系统 SHALL 以规范化 JSON payload 保存只追加的审计事件，并为 payload 和事件整体生成可重算的 SHA-256 摘要；同一 task 的事件通过 `prev_event_digest` 形成摘要链。保留期内普通应用路径与数据库客户端 MUST NOT 更新或删除既有事件；仅定时清理的受控事务可删除达到 30 天期限的审计事件及因到期任务整体删除而必须连带的同任务事件。

#### Scenario: Equivalent JSON produces stable payload digest

- **WHEN** 使用字段顺序不同但语义相同的 JSON 创建审计事件
- **THEN** 规范化后的 payload digest 一致

#### Scenario: Tampered payload is detected

- **WHEN** 读取审计事件后 payload 或摘要被修改
- **THEN** 完整性校验失败并拒绝将其作为可信审计事件返回

#### Scenario: Audit events cannot be updated or ordinarily deleted

- **WHEN** 应用普通路径或数据库客户端尝试更新或删除既有审计事件
- **THEN** 操作失败，保留期内的历史事件保持不变

#### Scenario: Scheduled cleanup removes expired audit chain

- **WHEN** 受控清理事务处理到期任务或到期的无任务审计记录
- **THEN** 系统删除符合期限和依赖规则的记录，并且普通写路径仍保持只追加

## ADDED Requirements

### Requirement: Expired outbox records are removed regardless of delivery state

系统 SHALL 按 `governance.outbox_event.updated_at` 清理超过 30 天的 outbox 记录，MUST NOT 以 `delivered_at` 是否为空作为保留条件。

#### Scenario: Pending record expires

- **WHEN** 待投递 outbox 记录的 `updated_at` 早于截止时间
- **THEN** 本次清理删除该记录并计数
