CREATE TABLE packet.packet_draft (
  draft_id uuid PRIMARY KEY,
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  draft_no integer NOT NULL,
  scope_revision_id uuid NOT NULL REFERENCES task.scope_revision(scope_revision_id),
  runtime_bundle_id uuid NOT NULL REFERENCES governance.runtime_bundle(runtime_bundle_id),
  canonical_content jsonb NOT NULL,
  content_digest char(64) NOT NULL,
  status varchar(20) NOT NULL CHECK (status IN ('CURRENT','STALE','WITHDRAWN','PUBLISHED')),
  created_at timestamptz NOT NULL DEFAULT now(),
  UNIQUE(task_id, draft_no)
);

CREATE TABLE packet.packet_review (
  review_id uuid PRIMARY KEY,
  draft_id uuid NOT NULL REFERENCES packet.packet_draft(draft_id),
  reviewer_user_id uuid NOT NULL REFERENCES iam.agent_user(user_id),
  content_digest char(64) NOT NULL,
  scope_revision_id uuid NOT NULL REFERENCES task.scope_revision(scope_revision_id),
  runtime_bundle_id uuid NOT NULL REFERENCES governance.runtime_bundle(runtime_bundle_id),
  decision varchar(20) NOT NULL CHECK (decision IN ('APPROVE','REJECT')),
  reviewed_at timestamptz NOT NULL,
  comment text
);

CREATE TABLE packet.packet_revision (
  packet_revision_id uuid PRIMARY KEY,
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  revision_no integer NOT NULL,
  draft_id uuid NOT NULL UNIQUE REFERENCES packet.packet_draft(draft_id),
  canonical_content jsonb NOT NULL,
  html_snapshot text NOT NULL,
  content_digest char(64) NOT NULL,
  checked_at timestamptz NOT NULL,
  source_versions jsonb NOT NULL,
  coverage_summary jsonb NOT NULL,
  published_by uuid NOT NULL REFERENCES iam.agent_user(user_id),
  published_at timestamptz NOT NULL,
  supersedes_revision_id uuid REFERENCES packet.packet_revision(packet_revision_id),
  UNIQUE(task_id, revision_no)
);

CREATE TABLE packet.handoff (
  handoff_id uuid PRIMARY KEY,
  packet_revision_id uuid NOT NULL REFERENCES packet.packet_revision(packet_revision_id),
  owner_reference varchar(300) NOT NULL,
  external_process_reference varchar(500),
  downstream_status varchar(30) NOT NULL,
  handed_off_by uuid NOT NULL REFERENCES iam.agent_user(user_id),
  handed_off_at timestamptz NOT NULL
);

CREATE TABLE governance.audit_event (
  audit_event_id uuid PRIMARY KEY,
  task_id uuid,
  actor_user_id uuid,
  event_type varchar(100) NOT NULL,
  target_type varchar(100) NOT NULL,
  target_id varchar(200) NOT NULL,
  event_payload jsonb NOT NULL,
  payload_digest char(64) NOT NULL,
  occurred_at timestamptz NOT NULL,
  prev_event_digest char(64),
  event_digest char(64) NOT NULL UNIQUE
);

CREATE TABLE governance.outbox_event (
  outbox_id uuid PRIMARY KEY,
  aggregate_type varchar(100) NOT NULL,
  aggregate_id varchar(200) NOT NULL,
  event_type varchar(100) NOT NULL,
  payload jsonb NOT NULL,
  created_at timestamptz NOT NULL,
  delivered_at timestamptz,
  delivery_attempts integer NOT NULL DEFAULT 0
);
