-- Uniform audit fields for every domain table. User fields remain nullable for
-- system-generated records; non-null values must reference iam.agent_user.

ALTER TABLE iam.agent_user
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE iam.role_binding
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE governance.runtime_bundle
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE task.investigation_task
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE task.scope_revision
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE task.task_premise
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE task.investigation_step
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE task.step_approval
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE task.task_lease
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE task.tool_invocation
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE evidence.fact_snapshot
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE evidence.coverage_record
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE evidence.finding
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE evidence.finding_evidence
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE evidence.computation_witness
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE evidence.professional_confirmation
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE packet.packet_draft
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE packet.packet_review
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE packet.packet_revision
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE packet.handoff
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE governance.audit_event
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE governance.outbox_event
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE harness.session_state
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE harness.checkpoint
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE knowledge.embedding_set
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE knowledge.document
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE knowledge.chunk
  ADD COLUMN IF NOT EXISTS created_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS created_at timestamptz NOT NULL DEFAULT now(),
  ADD COLUMN IF NOT EXISTS updated_by uuid REFERENCES iam.agent_user(user_id),
  ADD COLUMN IF NOT EXISTS updated_at timestamptz NOT NULL DEFAULT now();
