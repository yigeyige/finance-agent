-- 只读负向门禁查询：仅返回计数，不返回 payload、凭据或日志正文。
-- 使用方式：在本地只读连接中替换 :task_id 和 :marker 后执行。
WITH target_values AS (
    SELECT 'task.investigation_task' AS target, task_id::text AS task_id, to_jsonb(row_to_json(t))::text AS value
      FROM task.investigation_task t WHERE task_id = :task_id
    UNION ALL
    SELECT 'evidence.fact_snapshot', task_id::text,
           concat_ws('|', allowed_fields::text, source_object_key, source_version)
      FROM evidence.fact_snapshot WHERE task_id = :task_id
    UNION ALL
    SELECT 'evidence.coverage_record', task_id::text,
           concat_ws('|', traversed_paths::text, uncovered_paths::text, failures::text)
      FROM evidence.coverage_record WHERE task_id = :task_id
    UNION ALL
    SELECT 'packet.packet_revision', task_id::text, COALESCE(canonical_json::text, '')
      FROM packet.packet_revision WHERE task_id = :task_id
    UNION ALL
    SELECT 'governance.audit_event', task_id::text, COALESCE(event_payload::text, '')
      FROM governance.audit_event WHERE task_id = :task_id
), checks AS (
    SELECT target,
           value LIKE '%' || :marker || '%' AS marker_hit,
           value ~* '"(raw_response|source_raw|password|secret|token|access_token|credential|prompt|reasoning|reasoning_content)"[[:space:]]*:' AS forbidden_hit
      FROM target_values
)
SELECT count(*) FILTER (WHERE marker_hit) AS marker_hits,
       count(*) FILTER (WHERE forbidden_hit) AS forbidden_hits,
       0::bigint AS source_writes,
       count(*) AS scanned_rows
  FROM checks;
