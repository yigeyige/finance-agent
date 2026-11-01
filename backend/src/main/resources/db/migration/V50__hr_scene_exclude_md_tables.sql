-- 人员配置离职检查：排除 basedata.t_md_* 组织主数据表。
UPDATE task.business_scene_skill
SET investigation_prompt = replace(
        investigation_prompt,
        $old$- 不要查询 basedata.sys_emp_job（已停用/不用于本场景）。$old$,
        $new$- 不要查询 basedata.sys_emp_job（已停用/不用于本场景）。
- 不要查询 basedata.t_md_* 系列表（组织主数据，本场景不使用）。$new$),
    updated_at = now()
WHERE skill_id = '6ec7774b-b226-4a7c-be5f-49349c3a14d8';
