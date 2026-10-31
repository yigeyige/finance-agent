-- 证据快照标记与审批流程场景提示词更新。
ALTER TABLE task.business_evidence_accum
  ADD COLUMN IF NOT EXISTS snapshot boolean NOT NULL DEFAULT false;

UPDATE task.business_scene_skill
SET investigation_prompt = investigation_prompt || ' 审批流程生成只依据用户说明中的节点、分支和角色代码；不要查询审核人、用户、角色主数据（如 sys_role、sys_user、sys_employee）来推断审批人，assignee 留空并列入 missingItems。只有需要校验角色代码时，查询 basedata.sys_value_set_item（VS_CODE 为 approval_config_role，代码取 VSI_KEY）；查不到时使用用户提供的角色代码并列入 missingItems，不要继续查询主数据或臆造。',
    updated_at = now()
WHERE skill_id = 'a6a050ae-b699-4ca0-af72-4381e990eda0'
  AND position('不要查询审核人' in investigation_prompt) = 0;
