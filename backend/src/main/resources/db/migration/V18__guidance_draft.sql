CREATE TABLE task.guidance_draft (
  guidance_draft_id uuid PRIMARY KEY,
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  draft_json jsonb NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  created_by uuid
);

CREATE INDEX idx_guidance_draft_task_created
  ON task.guidance_draft (task_id, created_at DESC);
