CREATE TABLE task.business_evidence_accum (
  evidence_accum_id uuid PRIMARY KEY,
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  round_no integer NOT NULL,
  evidence jsonb NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  UNIQUE(task_id, round_no)
);

CREATE INDEX idx_business_evidence_accum_task_round
  ON task.business_evidence_accum (task_id, round_no);

CREATE TABLE task.business_decision_packet (
  packet_id uuid PRIMARY KEY,
  task_id uuid NOT NULL REFERENCES task.investigation_task(task_id),
  revision integer NOT NULL,
  canonical_content jsonb NOT NULL,
  content_digest char(64) NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  UNIQUE(task_id, revision)
);

CREATE INDEX idx_business_decision_packet_task_revision
  ON task.business_decision_packet (task_id, revision);
