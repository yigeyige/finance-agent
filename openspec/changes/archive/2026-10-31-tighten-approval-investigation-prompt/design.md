## Decisions

### 1. 先定位 procdef

提示词要求先用 `act_re_model.NAME_` 和 `act_re_procdef.key_` 定位流程定义，按版本降序选择最新/运行版本，只使用 `pd.id_` 作为 `procdef_id`。

### 2. 节点和流转分开查

- 节点：`psc_act_config` by `procdef_id`
- 流转：`psc_act_sequence_flow` by `procdef_id`
- 禁止两张一对多表同时 join；超过 30 行停止细化。

### 3. 保留既有规则

角色值集、BPMN 样例只参考结构、自检、禁止审核人主数据、禁止 `basedata.t_org_process_template` 全部保留。

### 4. 标题与流程名区分

从用户说明提取流程名，不直接用任务标题；不一致时记录 missingItems。

## Risks

- 提示词变长 → 结构化分节，减少重复。
- 模型仍写宽 SQL → 后续可加代码级 SQL 守卫。
