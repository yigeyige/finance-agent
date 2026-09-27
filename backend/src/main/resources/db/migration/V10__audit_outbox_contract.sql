-- Strengthen the pre-existing audit and local outbox tables without rewriting history.

ALTER TABLE governance.audit_event
  ADD COLUMN IF NOT EXISTS canonicalization_version varchar(40);

UPDATE governance.audit_event
SET canonicalization_version = 'audit-v1'
WHERE canonicalization_version IS NULL;

ALTER TABLE governance.audit_event
  ALTER COLUMN canonicalization_version SET NOT NULL;

ALTER TABLE governance.audit_event
  ADD CONSTRAINT ck_audit_payload_digest_hex
    CHECK (payload_digest ~ '^[0-9a-fA-F]{64}$'),
  ADD CONSTRAINT ck_audit_event_digest_hex
    CHECK (event_digest ~ '^[0-9a-fA-F]{64}$'),
  ADD CONSTRAINT ck_audit_prev_digest_hex
    CHECK (prev_event_digest IS NULL OR prev_event_digest ~ '^[0-9a-fA-F]{64}$');

ALTER TABLE governance.outbox_event
  ADD COLUMN IF NOT EXISTS idempotency_key varchar(200);

UPDATE governance.outbox_event
SET idempotency_key = outbox_id::text
WHERE idempotency_key IS NULL;

ALTER TABLE governance.outbox_event
  ALTER COLUMN idempotency_key SET NOT NULL,
  ADD CONSTRAINT uq_outbox_idempotency_key UNIQUE (idempotency_key),
  ADD CONSTRAINT ck_outbox_delivery_attempts_nonnegative CHECK (delivery_attempts >= 0);

CREATE INDEX IF NOT EXISTS ix_outbox_pending_delivery
  ON governance.outbox_event (created_at, outbox_id)
  WHERE delivered_at IS NULL;

CREATE OR REPLACE FUNCTION governance.reject_audit_mutation()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
  RAISE EXCEPTION 'governance.audit_event is append-only';
END;
$$;

DROP TRIGGER IF EXISTS trg_audit_event_immutable ON governance.audit_event;

CREATE TRIGGER trg_audit_event_immutable
BEFORE UPDATE OR DELETE ON governance.audit_event
FOR EACH ROW EXECUTE FUNCTION governance.reject_audit_mutation();
