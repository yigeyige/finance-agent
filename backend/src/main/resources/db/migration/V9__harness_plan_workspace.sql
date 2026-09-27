ALTER TABLE harness.checkpoint
  ADD COLUMN IF NOT EXISTS user_id uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS scope_revision_id uuid REFERENCES task.scope_revision(scope_revision_id),
  ADD COLUMN IF NOT EXISTS state_schema_version varchar(80);

ALTER TABLE harness.session_state
  ADD COLUMN IF NOT EXISTS state_schema_version varchar(80);

UPDATE harness.checkpoint checkpoint
SET user_id = session.user_id
FROM harness.session_state session
WHERE checkpoint.session_id = session.session_id
  AND checkpoint.user_id IS NULL;

ALTER TABLE harness.checkpoint
  ALTER COLUMN user_id SET NOT NULL;

CREATE TABLE harness.plan_revision (
  plan_revision_id uuid PRIMARY KEY,
  session_id uuid NOT NULL REFERENCES harness.session_state(session_id),
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  user_id uuid NOT NULL REFERENCES iam.agent_user(user_id),
  revision_no integer NOT NULL CHECK (revision_no > 0),
  plan_schema_version varchar(80) NOT NULL,
  plan_ref jsonb NOT NULL,
  plan_digest char(64) NOT NULL,
  created_by uuid REFERENCES iam.agent_user(user_id),
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_by uuid REFERENCES iam.agent_user(user_id),
  updated_at timestamptz NOT NULL DEFAULT now(),
  UNIQUE(session_id, revision_no)
);

CREATE TABLE harness.workspace_ref (
  workspace_ref_id uuid PRIMARY KEY,
  session_id uuid NOT NULL REFERENCES harness.session_state(session_id),
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  user_id uuid NOT NULL REFERENCES iam.agent_user(user_id),
  relative_path varchar(1000) NOT NULL,
  object_ref varchar(1000) NOT NULL,
  content_digest char(64) NOT NULL,
  media_type varchar(200),
  created_by uuid REFERENCES iam.agent_user(user_id),
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_by uuid REFERENCES iam.agent_user(user_id),
  updated_at timestamptz NOT NULL DEFAULT now(),
  UNIQUE(session_id, relative_path)
);
