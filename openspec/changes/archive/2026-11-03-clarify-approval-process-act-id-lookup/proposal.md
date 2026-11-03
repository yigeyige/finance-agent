## Why

「审批流程生成-隆地」调查提示词将已有流程结构检查和全新流程 `act_id` 示例查询放在同一条限制下，容易使模型把按名称查询 `psc_act_config` 视为被禁止。需要明确两种查询用途，让新流程继续获得参考标识，同时避免把历史流程结构当成目标事实。

## What Changes

- 明确已有流程结构检查须先取得 `procdef_id`；没有时跳过对 `psc_act_re_procdef`、`psc_act_config`、`psc_act_sequence_flow` 的结构查询。
- 明确全新流程可按节点名称定向查询 `psc_act_config`，仅将结果用于参考 `act_id`。
- 新增 Flyway 迁移更新场景 `investigation_prompt`，保留其余提示词内容。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `approval-process-prompts`：区分已有流程结构查询与全新流程的节点标识参考查询。

## Impact

- 数据：更新 `task.business_scene_skill` 中该场景的 `investigation_prompt`。
- 行为：仅改变调查阶段对 EMS 表的查询指令；不修改 Java、前端、接口、表结构或文件生成提示词。
