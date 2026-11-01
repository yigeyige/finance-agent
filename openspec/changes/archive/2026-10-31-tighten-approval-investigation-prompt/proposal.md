## Why

“审批流程生成-隆地”调查提示词经过多次追加，存在损坏文本，且未约束流程定位和一对多表 join，导致宽泛查询和证据行数放大。

## What Changes

- 重写场景 `investigation_prompt`，按“定位 procdef → 节点 → 流转”组织。
- 明确 `act_re_model.NAME_`、`act_re_procdef.key_/id_`、`psc_act_re_procdef.procdef_id` 关系。
- 禁止 `psc_act_config` 与 `psc_act_sequence_flow` 直接 join。
- 保留角色值集、BPMN 样例参考、自检和禁止审核人主数据规则。
- 新增 Flyway V37 更新提示词。

## Impact

- 数据：Flyway V37。
- 行为：模型选表和查询规划更聚焦，减少无关证据。
