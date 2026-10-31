-- 固化隆地审批流程调查阶段的禁查表规则，不覆盖管理员调整过的其它提示词。
UPDATE task.business_scene_skill
SET investigation_prompt = rtrim(investigation_prompt) || E'\n'
        || '调查过程中不要查询 basedata.t_org_process_template 表，也不要将该表作为选表或 SQL 查询对象。',
    updated_at = now()
WHERE skill_id = 'a6a050ae-b699-4ca0-af72-4381e990eda0'
  AND name = '审批流程生成-隆地'
  AND deleted_at IS NULL
  AND position('调查过程中不要查询 basedata.t_org_process_template' in investigation_prompt) = 0;
