## Decisions

### 1. 候选表 hint
在审批流程场景的 schema searchText 中加入 `act_re_model act_re_procdef psc_act_re_procdef psc_act_config psc_act_sequence_flow approval_config_role sys_value_set_item`。

### 2. 提示词长度
`BusinessSceneSkill` 与请求 DTO 的 `investigationPrompt` 上限提高到 8000；V40 将数据库列改为 text。

### 3. 标题兜底与链式约束
- 说明无流程名时使用任务标题，并记录待确认。
- `act_re_model/act_re_procdef` 查询为空时停止后续节点/流转查询。

### 4. 504 重试
`DeepSeekBusinessModelClient` 检测 `status 504` 后重试一次，避免偶发网关超时直接失败。
