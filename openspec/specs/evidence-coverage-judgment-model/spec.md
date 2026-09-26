# Evidence, Coverage, and Judgment Model

## Purpose

定义事实快照、范围覆盖度、发现及其证据链、计算见证和专业确认的持久化与发布前置条件。

## Requirements

### Requirement: Evidence snapshot persistence

系统 SHALL 保存与任务、步骤、来源绑定和字段策略版本关联的最小事实快照；快照只能包含已裁剪字段，并记录检查时间、生效分类、来源版本、结果摘要和审计字段。

#### Scenario: Snapshot is linked to task and step
- **WHEN** 写入事实快照
- **THEN** 快照必须引用存在的任务、步骤和来源绑定，并保存字段策略版本与 schema 版本

#### Scenario: Invalid effective range is rejected
- **WHEN** 快照的生效开始时间晚于生效结束时间
- **THEN** 领域边界拒绝该快照

### Requirement: Coverage records

系统 SHALL 保存范围遍历的查询版本、条件摘要、分页/计数、路径覆盖、失败信息、结果集摘要和 `COMPLETE`/`PARTIAL`/`UNKNOWN` 覆盖度。

#### Scenario: Coverage status is controlled
- **WHEN** 创建覆盖记录
- **THEN** 覆盖度只能使用 `COMPLETE`、`PARTIAL` 或 `UNKNOWN`

#### Scenario: Negative coverage counts are rejected
- **WHEN** 页数、预期总数或观测总数为负数
- **THEN** 领域边界拒绝该覆盖记录

### Requirement: Finding evidence chain

系统 SHALL 保存 finding 的判断方式、生效分类、范围版本和状态，并允许 finding 关联多个事实快照；确定性 finding 必须有计算见证，专业判断必须有逐项人工确认，才可进入可发布状态。

#### Scenario: Finding references evidence before publish
- **WHEN** 请求将 finding 进入可发布状态
- **THEN** finding 必须为已确认状态且至少关联一条事实快照

#### Scenario: Deterministic finding requires witness
- **WHEN** finding 的判断方式为 `DETERMINISTIC` 且没有计算见证
- **THEN** 发布门禁拒绝该 finding

#### Scenario: Professional finding requires confirmation
- **WHEN** finding 的判断方式为 `HUMAN_CONFIRMED` 且没有人工确认
- **THEN** 发布门禁拒绝该 finding

### Requirement: Computation witness and professional confirmation

系统 SHALL 保存计算见证的算法/规则版本、输入输出摘要、快照 ID 集合和复算状态；专业确认 SHALL 保存确认人、时间、范围版本、决定、理由和证据引用。

#### Scenario: Witness is bound to one finding
- **WHEN** 为同一 finding 写入第二条计算见证
- **THEN** 数据库唯一约束拒绝重复见证

#### Scenario: Confirmation is bound to one finding
- **WHEN** 为同一 finding 写入第二条专业确认
- **THEN** 数据库唯一约束拒绝重复确认
