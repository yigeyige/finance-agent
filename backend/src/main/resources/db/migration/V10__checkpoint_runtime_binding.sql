ALTER TABLE harness.checkpoint
  ADD COLUMN IF NOT EXISTS runtime_bundle_id uuid REFERENCES governance.runtime_bundle(runtime_bundle_id);
