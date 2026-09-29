-- 只读恢复/幂等/租约证据查询；仅返回计数，不返回 payload。
-- 使用方式：在本地只读连接中替换 :task_id。
SELECT
  (SELECT count(*) FROM task.task_lease WHERE task_id = :task_id AND released_at IS NULL) AS active_leases,
  (SELECT count(*) FROM task.tool_invocation WHERE task_id = :task_id) AS invocation_count,
  (SELECT count(*) FROM harness.checkpoint WHERE task_id = :task_id) AS checkpoint_count,
  (SELECT count(*) FROM packet.packet_revision WHERE task_id = :task_id) AS packet_revision_count,
  (SELECT count(*) FROM governance.outbox_event o
     WHERE o.aggregate_type = 'packet_revision'
       AND o.aggregate_id IN (SELECT packet_revision_id::text FROM packet.packet_revision WHERE task_id = :task_id)) AS publication_outbox_count,
  (SELECT count(*) FROM governance.audit_event WHERE task_id = :task_id) AS audit_count;
