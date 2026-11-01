-- 任务附件留痕库：保存用户上传的原始文件、文本投影和校验信息。
CREATE TABLE IF NOT EXISTS task.task_attachment (
    attachment_id uuid PRIMARY KEY,
    task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
    file_name varchar(255) NOT NULL,
    content_type varchar(200) NOT NULL,
    size_bytes bigint NOT NULL,
    sha256 char(64) NOT NULL,
    content bytea NOT NULL,
    extracted_text text,
    truncated boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT uq_task_attachment_sha UNIQUE (task_id, sha256)
);

CREATE INDEX IF NOT EXISTS idx_task_attachment_task
    ON task.task_attachment(task_id, created_at);

COMMENT ON TABLE task.task_attachment IS '任务上传附件留痕库';
COMMENT ON COLUMN task.task_attachment.content IS '上传文件原始字节';
COMMENT ON COLUMN task.task_attachment.extracted_text IS '模型实际使用的文本投影';
COMMENT ON COLUMN task.task_attachment.sha256 IS '原始文件 SHA-256，用于任务内去重';
