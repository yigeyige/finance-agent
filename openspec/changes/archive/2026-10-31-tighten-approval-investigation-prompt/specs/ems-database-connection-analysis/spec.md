## ADDED Requirements

### Requirement: Approval investigation query scope

审批流程调查提示词 SHALL 先定位唯一 `procdef_id`，再分别查询节点和流转；MUST 禁止把 `psc_act_config` 与 `psc_act_sequence_flow` 按 `procdef_id` 直接 join；MUST 禁止无流程名称或 `procdef_id` 条件的全表查询；单次查询超过 30 行 SHALL 停止并细化条件。

#### Scenario: Resolve process definition
- **WHEN** 用户说明包含流程名称
- **THEN** 先按 `act_re_model.NAME_` 和 `act_re_procdef.key_` 定位唯一 `procdef_id`，并记录使用的版本

#### Scenario: Query nodes and flows separately
- **WHEN** 已取得 `procdef_id`
- **THEN** 分别查询 `psc_act_config` 和 `psc_act_sequence_flow`，不在一张 SQL 中同时 join

#### Scenario: Row amplification
- **WHEN** 查询返回超过 30 行
- **THEN** 停止并细化条件，不把全部行注入证据

#### Scenario: Title differs from process name
- **WHEN** 任务标题与用户说明中的流程名不一致
- **THEN** 以用户说明中的流程名为准，并在 missingItems 记录不一致
