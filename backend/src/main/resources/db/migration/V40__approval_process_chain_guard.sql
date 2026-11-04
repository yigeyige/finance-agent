-- 调查提示词已超过原 varchar(2000) 限制，改为 text。
ALTER TABLE task.business_scene_skill ALTER COLUMN investigation_prompt TYPE text;

-- 审批流程调查：标题兜底流程名；前置流程未命中时停止下游查询。
UPDATE task.business_scene_skill
SET investigation_prompt = replace(
        replace(investigation_prompt,
            $old1$1. 从用户业务说明中提取流程名称（例如“8号流程”）；不要直接把任务标题当成流程名。标题与流程名不一致时，以说明中的流程名为准，并在 missingItems 记录不一致。$old1$,
            $new1$1. 优先从用户业务说明中提取流程名称；如果说明中没有流程名，直接使用任务标题作为流程名，并在 missingItems 标注“流程名来自标题，待确认”。标题与说明中的流程名不一致时，以说明为准。$new1$),
        $old2$5. 禁止在没有流程名称或 procdef_id 条件时全表查询 psc_act_re_procdef、psc_act_config、psc_act_sequence_flow。$old2$,
        $new2$5. 禁止在没有流程名称或 procdef_id 条件时全表查询 psc_act_re_procdef、psc_act_config、psc_act_sequence_flow。如果 act_re_model/act_re_procdef 查询为空，立即停止后续 psc_act_config、psc_act_sequence_flow 查询，把未定位流程写入 missingItems，不要用空 procdef_id 继续查下游表。$new2$),
    updated_at = now()
WHERE skill_id = 'a6a050ae-b699-4ca0-af72-4381e990eda0';
