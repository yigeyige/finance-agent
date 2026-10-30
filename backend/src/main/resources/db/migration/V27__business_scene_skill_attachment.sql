-- 业务场景 Skill 附件：保存上传文件的文本投影与手填摘要，供调查阶段模型按需使用。

CREATE TABLE task.business_scene_skill_attachment (
    attachment_id uuid PRIMARY KEY,
    skill_id uuid NOT NULL REFERENCES task.business_scene_skill(skill_id),
    file_name varchar(255) NOT NULL,
    content_type varchar(100) NOT NULL,
    summary varchar(500) NOT NULL,
    content_text text NOT NULL,
    created_by uuid NOT NULL,
    created_at timestamptz NOT NULL,
    updated_by uuid NOT NULL,
    updated_at timestamptz NOT NULL
);

CREATE INDEX idx_business_scene_skill_attachment_skill
    ON task.business_scene_skill_attachment(skill_id);

COMMENT ON TABLE task.business_scene_skill_attachment IS '业务场景技能附件';
COMMENT ON COLUMN task.business_scene_skill_attachment.file_name IS '附件文件名';
COMMENT ON COLUMN task.business_scene_skill_attachment.content_type IS '附件内容类型';
COMMENT ON COLUMN task.business_scene_skill_attachment.summary IS '管理员填写的附件摘要';
COMMENT ON COLUMN task.business_scene_skill_attachment.content_text IS '附件文本投影正文';
