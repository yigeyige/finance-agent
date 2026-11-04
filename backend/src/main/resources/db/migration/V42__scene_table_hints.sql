-- 场景级候选表配置，替代代码中的表名前缀推断。
ALTER TABLE task.business_scene_skill
  ADD COLUMN IF NOT EXISTS table_hints text;

UPDATE task.business_scene_skill
SET table_hints = 'act_re_model act_re_procdef act_re_deployment psc_act_re_procdef psc_act_config psc_act_sequence_flow basedata.sys_value_set_item basedata.sys_value_set',
    updated_at = now()
WHERE skill_id = 'a6a050ae-b699-4ca0-af72-4381e990eda0'
  AND (table_hints IS NULL OR table_hints = '');
