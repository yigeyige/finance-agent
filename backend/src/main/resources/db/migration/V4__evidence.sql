CREATE TABLE evidence.fact_snapshot (
  snapshot_id uuid PRIMARY KEY,
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  step_id uuid NOT NULL REFERENCES task.investigation_step(step_id),
  source_binding_id uuid NOT NULL,
  source_object_type varchar(100) NOT NULL,
  source_object_key varchar(300) NOT NULL,
  allowed_fields jsonb NOT NULL,
  checked_at timestamptz NOT NULL,
  effective_from timestamptz,
  effective_to timestamptz,
  effective_class varchar(30) NOT NULL CHECK (effective_class IN ('CURRENT_EFFECTIVE','FUTURE_SCHEDULED','UNDETERMINED')),
  source_version varchar(300),
  result_digest char(64) NOT NULL,
  field_policy_version varchar(80) NOT NULL,
  schema_version varchar(30) NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE evidence.coverage_record (
  coverage_id uuid PRIMARY KEY,
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  step_id uuid NOT NULL REFERENCES task.investigation_step(step_id),
  scope_revision_id uuid NOT NULL REFERENCES task.scope_revision(scope_revision_id),
  query_template_version varchar(80) NOT NULL,
  query_condition_digest char(64) NOT NULL,
  enumeration_method varchar(100) NOT NULL,
  page_count integer,
  expected_total bigint,
  observed_total bigint,
  traversed_paths jsonb NOT NULL,
  uncovered_paths jsonb NOT NULL,
  failures jsonb NOT NULL,
  source_watermark varchar(300),
  result_set_digest char(64) NOT NULL,
  completeness varchar(20) NOT NULL CHECK (completeness IN ('COMPLETE','PARTIAL','UNKNOWN')),
  checked_at timestamptz NOT NULL
);

CREATE TABLE evidence.finding (
  finding_id uuid PRIMARY KEY,
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  finding_type varchar(50) NOT NULL,
  statement text NOT NULL,
  judgment_mode varchar(30) NOT NULL CHECK (judgment_mode IN ('DETERMINISTIC','HUMAN_CONFIRMED','UNKNOWN')),
  effective_class varchar(30) NOT NULL CHECK (effective_class IN ('CURRENT_EFFECTIVE','FUTURE_SCHEDULED','UNDETERMINED')),
  scope_revision_id uuid NOT NULL REFERENCES task.scope_revision(scope_revision_id),
  status varchar(20) NOT NULL CHECK (status IN ('DRAFT','CONFIRMED','STALE','WITHDRAWN')),
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE evidence.finding_evidence (
  finding_id uuid NOT NULL REFERENCES evidence.finding(finding_id),
  snapshot_id uuid NOT NULL REFERENCES evidence.fact_snapshot(snapshot_id),
  relation_type varchar(30) NOT NULL,
  PRIMARY KEY (finding_id, snapshot_id)
);

CREATE TABLE evidence.computation_witness (
  witness_id uuid PRIMARY KEY,
  finding_id uuid NOT NULL UNIQUE REFERENCES evidence.finding(finding_id),
  algorithm_version varchar(80) NOT NULL,
  rule_set_version varchar(80) NOT NULL,
  input_digest char(64) NOT NULL,
  output_digest char(64) NOT NULL,
  snapshot_ids jsonb NOT NULL,
  computed_at timestamptz NOT NULL,
  recompute_status varchar(20) NOT NULL CHECK (recompute_status IN ('NOT_RUN','MATCHED','MISMATCHED','UNAVAILABLE')),
  recomputed_at timestamptz
);

CREATE TABLE evidence.professional_confirmation (
  confirmation_id uuid PRIMARY KEY,
  finding_id uuid NOT NULL UNIQUE REFERENCES evidence.finding(finding_id),
  confirmed_by uuid NOT NULL REFERENCES iam.agent_user(user_id),
  confirmed_at timestamptz NOT NULL,
  scope_revision_id uuid NOT NULL REFERENCES task.scope_revision(scope_revision_id),
  decision varchar(20) NOT NULL CHECK (decision IN ('CONFIRM','REJECT','NEEDS_MORE_EVIDENCE')),
  rationale text NOT NULL,
  evidence_refs jsonb NOT NULL
);
