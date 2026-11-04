-- 在调查提示词中显式列出候选表，供代码通用提取，不再在代码中硬编码表名。
UPDATE task.business_scene_skill
SET investigation_prompt = investigation_prompt || $hint$

【候选表】
act_re_model、act_re_procdef、act_re_deployment、psc_act_re_procdef、psc_act_config、psc_act_sequence_flow、basedata.sys_value_set_item、basedata.sys_value_set$hint$,
    updated_at = now()
WHERE skill_id = 'a6a050ae-b699-4ca0-af72-4381e990eda0'
  AND position('【候选表】' in investigation_prompt) = 0;
