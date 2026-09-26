CREATE TABLE harness.session_state (
  session_id uuid PRIMARY KEY,
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  user_id uuid NOT NULL REFERENCES iam.agent_user(user_id),
  harness_version varchar(80) NOT NULL,
  state_ref jsonb NOT NULL,
  state_digest char(64) NOT NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL
);

CREATE TABLE harness.checkpoint (
  checkpoint_id uuid PRIMARY KEY,
  session_id uuid NOT NULL REFERENCES harness.session_state(session_id),
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  step_id uuid REFERENCES task.investigation_step(step_id),
  approval_id uuid REFERENCES task.step_approval(approval_id),
  checkpoint_type varchar(50) NOT NULL,
  state_ref jsonb NOT NULL,
  state_digest char(64) NOT NULL,
  created_at timestamptz NOT NULL
);
