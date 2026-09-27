# Packet Decision Governance Model

## Purpose

定义决策包草稿、复核、不可变发布修订和交接的权威内容与访问约束。

## Requirements

### Requirement: Decision packet JSON authority

系统 SHALL 保存 packet 草稿和发布修订的规范 JSON 内容，并将 HTML 仅作为与该 JSON 同步生成的快照；草稿和修订必须保存内容摘要、范围修订和 runtime bundle 绑定。

#### Scenario: Draft requires authoritative JSON
- **WHEN** 创建 packet 草稿
- **THEN** canonical JSON、schema version、content digest、scope revision 和 runtime bundle 均不能为空且 JSON 必须可严格解析

#### Scenario: Published revision keeps HTML as snapshot
- **WHEN** 创建发布修订
- **THEN** 修订同时保存 canonical JSON 和非空 HTML 快照，并保存同一内容摘要

### Requirement: Review binds exact versions

系统 SHALL 要求 packet review 绑定被复核草稿的准确内容 digest、scope revision 和 runtime bundle，并只允许对当前草稿执行复核。

#### Scenario: Matching review is accepted
- **WHEN** review 的 digest、scope revision 和 runtime bundle 与草稿一致且草稿状态为 CURRENT
- **THEN** review 可以按 APPROVE 或 REJECT 决策写入

#### Scenario: Mismatched review is rejected
- **WHEN** review 任一绑定值与草稿不一致
- **THEN** 领域边界拒绝该 review

### Requirement: Immutable published revisions

系统 SHALL 为同一任务维护唯一递增 revision number；已发布修订只能追加，不能通过 revision 仓储更新或删除，更正必须创建新修订并通过 supersedes 链关联旧修订。

#### Scenario: Revision number is unique per task
- **WHEN** 同一任务写入重复 revision number
- **THEN** 数据库唯一约束拒绝重复记录

#### Scenario: Correction creates a new revision
- **WHEN** 已发布内容需要更正
- **THEN** 系统创建新的 revision record 并引用被替代的 revision，旧 revision 保持不变

#### Scenario: Published revision has no mutable repository operation
- **WHEN** 调用 packet revision 仓储
- **THEN** 仓储只提供插入和查询发布版本能力，不提供更新或删除已发布修订的方法

### Requirement: Published-only and handoff access

系统 SHALL 提供只返回已发布修订的任务查询，并要求 handoff 绑定发布修订、交接责任人和执行人。

#### Scenario: Viewer query hides drafts
- **WHEN** VIEWER 按任务查询 packet
- **THEN** 查询只返回已发布 revision，不返回 draft 或 review 内容

#### Scenario: Handoff references published revision
- **WHEN** 创建 handoff
- **THEN** handoff 必须引用 packet revision，并保存 owner reference、交接人和交接时间
