-- 只读证据查询。执行示例：psql "$env:FINANCE_AGENT_DB_URL" -v task_id='00000000-0000-4000-8000-000000000101' -f scripts/qa-core-synthetic-vertical-slice.sql
-- 连接串、账号和密码只从本地环境提供，不写入仓库。

\set ON_ERROR_STOP on

SELECT 'task' AS evidence_type, task_id::text AS object_id, status, row_version::text AS version,
       runtime_bundle_id::text AS runtime_bundle_id, updated_at
FROM task.investigation_task WHERE task_id = :'task_id'::uuid;

SELECT 'premise' AS evidence_type, premise_id::text AS object_id, premise_type AS status,
       subject_key AS safe_summary, provided_at
FROM task.task_premise WHERE task_id = :'task_id'::uuid ORDER BY provided_at;

SELECT 'scope_revision' AS evidence_type, scope_revision_id::text AS object_id,
       revision_no::text AS version, system_code AS status, scope_digest, confirmed_at
FROM task.scope_revision WHERE task_id = :'task_id'::uuid ORDER BY revision_no;

SELECT 'step' AS evidence_type, step_id::text AS object_id, sequence_no::text AS version,
       status, recommended::text, created_at, completed_at
FROM task.investigation_step WHERE task_id = :'task_id'::uuid ORDER BY sequence_no;

SELECT 'step_approval' AS evidence_type, approval_id::text AS object_id, revision_no::text AS version,
       status, approval_digest, scope_revision_id::text, approved_at, expires_at
FROM task.step_approval WHERE step_id IN (SELECT step_id FROM task.investigation_step WHERE task_id = :'task_id'::uuid)
ORDER BY approved_at;

SELECT 'lease' AS evidence_type, lease_id::text AS object_id, owner_instance AS status,
       acquired_at, expires_at, released_at
FROM task.task_lease WHERE task_id = :'task_id'::uuid ORDER BY acquired_at;

SELECT 'tool_invocation' AS evidence_type, invocation_id::text AS object_id, tool_code AS status,
       response_digest, started_at, completed_at, error_code
FROM task.tool_invocation WHERE task_id = :'task_id'::uuid ORDER BY started_at;

SELECT 'fact_snapshot' AS evidence_type, snapshot_id::text AS object_id, source_object_type AS status,
       source_object_key, effective_class, source_version, result_digest, checked_at
FROM evidence.fact_snapshot WHERE task_id = :'task_id'::uuid ORDER BY checked_at;

SELECT 'coverage' AS evidence_type, coverage_id::text AS object_id, completeness AS status,
       page_count, expected_total, observed_total, source_watermark, result_set_digest, checked_at
FROM evidence.coverage_record WHERE task_id = :'task_id'::uuid ORDER BY checked_at;

SELECT 'finding' AS evidence_type, finding_id::text AS object_id, finding_type AS status,
       judgment_mode, effective_class, scope_revision_id::text, created_at
FROM evidence.finding WHERE task_id = :'task_id'::uuid ORDER BY created_at;

SELECT 'witness' AS evidence_type, witness_id::text AS object_id, finding_id::text AS finding_id,
       algorithm_version, rule_set_version, input_digest, output_digest, recompute_status, computed_at
FROM evidence.computation_witness WHERE finding_id IN (SELECT finding_id FROM evidence.finding WHERE task_id = :'task_id'::uuid)
ORDER BY computed_at;

SELECT 'professional_confirmation' AS evidence_type, confirmation_id::text AS object_id,
       finding_id::text AS finding_id, decision AS status, scope_revision_id::text, confirmed_at
FROM evidence.professional_confirmation WHERE finding_id IN (SELECT finding_id FROM evidence.finding WHERE task_id = :'task_id'::uuid)
ORDER BY confirmed_at;

SELECT 'packet_draft' AS evidence_type, draft_id::text AS object_id, draft_no::text AS version,
       status, content_digest, scope_revision_id::text, created_at
FROM packet.packet_draft WHERE task_id = :'task_id'::uuid ORDER BY draft_no;

SELECT 'packet_review' AS evidence_type, review_id::text AS object_id, decision AS status,
       draft_id::text, content_digest, scope_revision_id::text, reviewed_at
FROM packet.packet_review WHERE draft_id IN (SELECT draft_id FROM packet.packet_draft WHERE task_id = :'task_id'::uuid)
ORDER BY reviewed_at;

SELECT 'scope_recheck' AS evidence_type, scope_recheck_id::text AS object_id, decision AS status,
       scope_revision_id::text, result_digest, checked_at
FROM packet.scope_recheck WHERE task_id = :'task_id'::uuid ORDER BY checked_at;

SELECT 'packet_revision' AS evidence_type, packet_revision_id::text AS object_id, revision_no::text AS version,
       content_digest, published_by::text, published_at, checked_at
FROM packet.packet_revision WHERE task_id = :'task_id'::uuid ORDER BY revision_no;

SELECT 'audit' AS evidence_type, audit_event_id::text AS object_id, event_type AS status,
       target_type, target_id, payload_digest, occurred_at
FROM governance.audit_event WHERE task_id = :'task_id'::uuid ORDER BY occurred_at;

SELECT 'outbox' AS evidence_type, outbox_id::text AS object_id, event_type AS status,
       aggregate_type, aggregate_id, created_at, delivered_at
FROM governance.outbox_event WHERE aggregate_id IN (SELECT task_id::text FROM task.investigation_task WHERE task_id = :'task_id'::uuid)
   OR aggregate_id IN (SELECT packet_revision_id::text FROM packet.packet_revision WHERE task_id = :'task_id'::uuid)
ORDER BY created_at;

SELECT 'handoff' AS evidence_type, handoff_id::text AS object_id, downstream_status AS status,
       packet_revision_id::text, owner_reference, external_process_reference, handed_off_at
FROM packet.handoff WHERE packet_revision_id IN (SELECT packet_revision_id FROM packet.packet_revision WHERE task_id = :'task_id'::uuid)
ORDER BY handed_off_at;
