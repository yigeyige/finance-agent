-- 区分已有流程结构查询与全新流程的 act_id 示例查询，仅更新调查提示词。
DO $migration$
BEGIN
    UPDATE task.business_scene_skill
    SET investigation_prompt = replace(
            replace(investigation_prompt,
                $old_structure$4. 禁止在没有流程名或 procdef_id 条件时全表查询 psc_act_re_procdef、psc_act_config、psc_act_sequence_flow。$old_structure$,
                $new_structure$4. 检查已有流程结构时，先按第 3 条定位并取得 procdef_id；未取得 procdef_id 时，跳过对 psc_act_re_procdef、psc_act_config、psc_act_sequence_flow 的流程结构查询。$new_structure$),
            $old_heading$【act_id 约定表（优先）】$old_heading$,
            $new_heading$【act_id 约定表（优先）】
全新流程为获取可参考的示例 act_id，可按节点名称定向查询 psc_act_config；查询结果仅作 act_id 参考，不作为目标流程的节点或流转事实。$new_heading$),
        updated_at = now()
    WHERE skill_id = 'a6a050ae-b699-4ca0-af72-4381e990eda0'
      AND position($old_structure$4. 禁止在没有流程名或 procdef_id 条件时全表查询 psc_act_re_procdef、psc_act_config、psc_act_sequence_flow。$old_structure$ in investigation_prompt) > 0
      AND position($old_heading$【act_id 约定表（优先）】$old_heading$ in investigation_prompt) > 0;

    IF NOT FOUND THEN
        RAISE EXCEPTION '审批流程调查提示词与 V52 预期不符，未应用 V53';
    END IF;
END
$migration$;
