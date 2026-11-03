## ADDED Requirements

### Requirement: 区分流程结构查询与新流程标识参考查询

「审批流程生成-隆地」的调查提示词 SHALL 将已有流程结构查询和全新流程 `act_id` 参考查询分别约束。检查已有流程结构时，MUST 先取得 `procdef_id`；没有 `procdef_id` 时 MUST 跳过对 `psc_act_re_procdef`、`psc_act_config`、`psc_act_sequence_flow` 的结构查询。生成全新流程时，SHALL 允许按节点名称定向查询 `psc_act_config` 取得参考 `act_id`，但 MUST NOT 把查询结果当作目标流程的节点或流转事实。

#### Scenario: 已有流程未取得定义标识

- **WHEN** 需要检查已有流程结构，但没有确认 `procdef_id`
- **THEN** 跳过三张表的结构查询，不用无范围的历史记录推断目标结构

#### Scenario: 全新流程获取节点标识参考

- **WHEN** 全新流程的节点未被 `act_id` 约定表覆盖
- **THEN** 可按节点名称定向查询 `psc_act_config` 参考 `act_id`，且不把命中的历史流程结构作为目标流程事实
