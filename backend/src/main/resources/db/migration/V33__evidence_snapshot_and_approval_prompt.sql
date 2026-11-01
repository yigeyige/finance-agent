-- 证据快照标记与审批流程场景提示词更新。
ALTER TABLE task.business_evidence_accum
  ADD COLUMN IF NOT EXISTS snapshot boolean NOT NULL DEFAULT false;
