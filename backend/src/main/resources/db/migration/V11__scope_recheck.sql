CREATE TABLE packet.scope_recheck (
  recheck_id uuid PRIMARY KEY,
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  scope_revision_id uuid NOT NULL REFERENCES task.scope_revision(scope_revision_id),
  checked_at timestamptz NOT NULL,
  decision varchar(30) NOT NULL CHECK (decision IN ('READY_TO_PUBLISH','STALE_DRAFT')),
  result_digest char(64) NOT NULL,
  diff_summary text NOT NULL,
  created_by uuid,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_by uuid,
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX ix_scope_recheck_task_checked_at
  ON packet.scope_recheck(task_id, checked_at DESC);
