-- 业务场景 Skill 字段收敛：去掉处理方式/业务场景，业务说明改为调查提示词，并新增决策包提示词。
ALTER TABLE task.business_scene_skill RENAME COLUMN description TO investigation_prompt;

ALTER TABLE task.business_scene_skill ALTER COLUMN investigation_prompt TYPE varchar(2000);

ALTER TABLE task.business_scene_skill
    ADD COLUMN decision_packet_prompt varchar(2000) NOT NULL DEFAULT '';

ALTER TABLE task.business_scene_skill DROP COLUMN handling_type;
ALTER TABLE task.business_scene_skill DROP COLUMN premise_type;

COMMENT ON COLUMN task.business_scene_skill.investigation_prompt IS '调查提示词';
COMMENT ON COLUMN task.business_scene_skill.decision_packet_prompt IS '决策包提示词';
