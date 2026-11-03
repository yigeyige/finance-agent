## ADDED Requirements

### Requirement: 新流程命名以任务标题为准

「审批流程生成-隆地」在新流程生成时 SHALL 以任务标题作为 `processPlan.name`；标题为空或明显不是流程名时，SHALL 使用业务说明中的流程名。附件中的“审批流程名称”MUST 仅作为参考，不得覆盖目标流程名。新流程在 `activiti_ems.psc_act_re_model` 未命中时 MUST NOT 作为阻塞待补项。

#### Scenario: 标题与附件名称不一致

- **WHEN** 任务标题为“测试2号出差流程”，附件名称为“出差申请”
- **THEN** `processPlan.name` 为“测试2号出差流程”，不产生“名称不一致”阻塞待补项

#### Scenario: 新流程未命中历史定义

- **WHEN** 按标题查询 `psc_act_re_model` 返回 0 行
- **THEN** 继续依据附件与约定表生成流程，不把未命中写成阻塞项

### Requirement: 角色码与 act_id 使用约定表优先、值集兜底

调查提示词 SHALL 优先使用约定表映射角色码和 act_id；未覆盖时 SHALL 查询 `sys_value_set_item` + `sys_value_set_item_lang`（`zh-TW` 优先、`zh-CN` 兜底）或 `psc_act_config`，并取第一条。查询无结果时 MUST 留空并写入 `missingItems`，不得猜测。`nodes[].id`、`nodeParameters.actId`、`departmentApprovals.approvalNode` SHALL 使用同一 act_id。

#### Scenario: 命中约定表

- **WHEN** 业务节点为“部门负责人”
- **THEN** 角色码取 `BMFZR`，act_id 取 `act_11`

#### Scenario: 约定表未覆盖

- **WHEN** 业务节点不在约定表中
- **THEN** 先查值集/act_id 字典取第一条；无结果时留空并写入 `missingItems`

### Requirement: 角色分档不生成空审批规则

当附件未提供审批金额范围且按起草角色分档时，系统 MUST NOT 生成空壳 `审批规则.json`；角色分档 SHALL 通过 BPMN 排他网关表达，出边条件为 `roleCode == <角色码>`。仅当提供审批金额范围时，`审批规则.json` SHALL 按 EMS 契约输出 `workflowLineKey`、`workflowLineValue`、`applyAmountMin/Max`、`currency`。

#### Scenario: 角色分档

- **WHEN** 附件审批金额范围为空白，审批矩阵按起草角色标记 Y
- **THEN** 生成带 `roleCode` 条件的排他网关，不生成 `审批规则.json`

#### Scenario: 金额分档

- **WHEN** 附件提供审批金额范围
- **THEN** `审批规则.json` 的 `lineList` 包含金额档位和币种
