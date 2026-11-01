-- 人员配置场景强制包含 basedata.sys_register_user 查询。
UPDATE task.business_scene_skill
SET investigation_prompt = replace(
        investigation_prompt,
        $old$【人员定位】
1. 以 basedata.sys_register_user 为入口，$old$,
        $new$【人员定位】
1. 本场景第一步必须查询 basedata.sys_register_user，不得返回空 steps；以 basedata.sys_register_user 为入口，$new$),
    updated_at = now()
WHERE skill_id = '6ec7774b-b226-4a7c-be5f-49349c3a14d8';

UPDATE governance.prompt_template
SET content = content || ' 如果场景调查提示词声明必查表，steps 必须包含该表，不得输出空计划。'
WHERE template_code = 'PLAN_QUERY'
  AND position('必查表' in content) = 0;
