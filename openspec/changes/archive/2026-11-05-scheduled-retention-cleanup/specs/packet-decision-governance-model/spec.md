## MODIFIED Requirements

### Requirement: Immutable published revisions

系统 SHALL 为同一任务维护唯一递增 revision number；已发布修订在 30 天保留期内只能追加，不能通过 revision 仓储更新或删除，更正必须创建新修订并通过 supersedes 链关联旧修订。仅到期清理事务可按依赖顺序删除到期修订及其交接、复核等引用记录。

#### Scenario: Revision number is unique per task

- **WHEN** 同一任务写入重复 revision number
- **THEN** 数据库唯一约束拒绝重复记录

#### Scenario: Correction creates a new revision

- **WHEN** 已发布内容需要更正
- **THEN** 系统创建新的 revision record 并引用被替代的 revision，旧 revision 在保留期内保持不变

#### Scenario: Published revision has no mutable repository operation

- **WHEN** 调用 packet revision 仓储
- **THEN** 仓储只提供插入和查询发布版本能力，不提供更新或删除已发布修订的方法

#### Scenario: Expired revision is cleaned

- **WHEN** 定时清理发现已发布修订到期
- **THEN** 系统按外键依赖清理该修订及必要关联记录，未到期且无依赖的修订继续保留
