CREATE TABLE task.investigation_task (
  task_id uuid PRIMARY KEY,
  title varchar(300) NOT NULL,
  original_question text NOT NULL,
  initiator_user_id uuid NOT NULL REFERENCES iam.agent_user(user_id),
  status varchar(50) NOT NULL CHECK (status IN (
    'DRAFT','WAITING_SCOPE_CONFIRMATION','READY','WAITING_STEP_APPROVAL','RUNNING_STEP',
    'ANALYZING','WAITING_PROFESSIONAL_CONFIRMATION','DRAFT_PACKET','WAITING_REVIEW',
    'RECHECKING_SCOPE','READY_TO_PUBLISH','PUBLISHED','HANDED_OFF','CLOSED',
    'PAUSED_MODEL_UNAVAILABLE','PAUSED_BUDGET','PAUSED_SOURCE_UNAVAILABLE',
    'STALE_APPROVAL','FAILED_RECOVERABLE','CANCELLED')),
  runtime_bundle_id uuid NOT NULL REFERENCES governance.runtime_bundle(runtime_bundle_id),
  current_scope_revision_id uuid,
  current_step_id uuid,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  row_version bigint NOT NULL DEFAULT 0,
  closed_at timestamptz
);

CREATE TABLE task.scope_revision (
  scope_revision_id uuid PRIMARY KEY,
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  revision_no integer NOT NULL,
  system_code varchar(100) NOT NULL,
  object_selector jsonb NOT NULL,
  effective_time_range jsonb NOT NULL,
  included_paths jsonb NOT NULL,
  excluded_paths jsonb NOT NULL,
  allowed_read_fields jsonb NOT NULL,
  allowed_retain_fields jsonb NOT NULL,
  allowed_model_fields jsonb NOT NULL,
  confirmed_by uuid NOT NULL REFERENCES iam.agent_user(user_id),
  confirmed_at timestamptz NOT NULL,
  scope_digest char(64) NOT NULL,
  schema_version varchar(30) NOT NULL,
  UNIQUE(task_id, revision_no),
  CHECK (jsonb_typeof(object_selector) = 'object')
);

ALTER TABLE task.investigation_task
  ADD CONSTRAINT fk_task_scope
  FOREIGN KEY (current_scope_revision_id) REFERENCES task.scope_revision(scope_revision_id);

CREATE TABLE task.task_premise (
  premise_id uuid PRIMARY KEY,
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  premise_type varchar(50) NOT NULL,
  subject_key varchar(200) NOT NULL,
  statement text NOT NULL,
  provided_by uuid NOT NULL REFERENCES iam.agent_user(user_id),
  source_description text NOT NULL,
  provided_at timestamptz NOT NULL,
  independently_verified boolean NOT NULL DEFAULT false
);

CREATE TABLE task.investigation_step (
  step_id uuid PRIMARY KEY,
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  sequence_no integer NOT NULL,
  goal text NOT NULL,
  recommended boolean NOT NULL,
  status varchar(30) NOT NULL CHECK (status IN (
    'PROPOSED','WAITING_APPROVAL','APPROVED','RUNNING','SUCCEEDED','PARTIAL',
    'FAILED','CANCELLED','STALE')),
  created_at timestamptz NOT NULL DEFAULT now(),
  completed_at timestamptz,
  UNIQUE(task_id, sequence_no)
);

ALTER TABLE task.investigation_task
  ADD CONSTRAINT fk_task_current_step
  FOREIGN KEY (current_step_id) REFERENCES task.investigation_step(step_id);

CREATE TABLE task.step_approval (
  approval_id uuid PRIMARY KEY,
  step_id uuid NOT NULL REFERENCES task.investigation_step(step_id),
  revision_no integer NOT NULL,
  approved_by uuid NOT NULL REFERENCES iam.agent_user(user_id),
  approved_at timestamptz NOT NULL,
  expires_at timestamptz,
  goal_hash char(64) NOT NULL,
  source_binding_id uuid NOT NULL,
  scope_revision_id uuid NOT NULL REFERENCES task.scope_revision(scope_revision_id),
  field_policy_version varchar(80) NOT NULL,
  query_template_version varchar(80) NOT NULL,
  max_calls integer NOT NULL CHECK (max_calls > 0),
  used_calls integer NOT NULL DEFAULT 0 CHECK (used_calls >= 0),
  approval_digest char(64) NOT NULL,
  status varchar(20) NOT NULL CHECK (status IN ('ACTIVE','CONSUMED','EXPIRED','REVOKED','STALE')),
  UNIQUE(step_id, revision_no)
);

CREATE TABLE task.task_lease (
  lease_id uuid PRIMARY KEY,
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  step_id uuid NOT NULL REFERENCES task.investigation_step(step_id),
  owner_instance varchar(200) NOT NULL,
  acquired_at timestamptz NOT NULL,
  heartbeat_at timestamptz NOT NULL,
  expires_at timestamptz NOT NULL,
  released_at timestamptz
);

CREATE UNIQUE INDEX ux_one_active_lease_per_task
  ON task.task_lease(task_id) WHERE released_at IS NULL;

CREATE TABLE task.tool_invocation (
  invocation_id uuid PRIMARY KEY,
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  step_id uuid NOT NULL REFERENCES task.investigation_step(step_id),
  approval_id uuid NOT NULL REFERENCES task.step_approval(approval_id),
  tool_code varchar(100) NOT NULL,
  idempotency_key varchar(300) NOT NULL UNIQUE,
  request_digest char(64) NOT NULL,
  response_digest char(64),
  status varchar(30) NOT NULL CHECK (status IN ('INTENT_RECORDED','RUNNING','SUCCEEDED','PARTIAL','FAILED','UNKNOWN')),
  started_at timestamptz NOT NULL,
  completed_at timestamptz,
  error_code varchar(100)
);
