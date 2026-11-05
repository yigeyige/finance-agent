package cn.com.crc.ems.finance.agent.retention;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @Description: 固定列出业务运行记录的清理表和时间字段，禁止把配置表带入批量删除。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-04 20:00:00
 */
final class RetentionCatalog {
    private static final List<Table> TABLES = List.of(
            updated("evidence", "computation_witness"),
            updated("evidence", "finding_evidence"),
            updated("evidence", "professional_confirmation"),
            updated("evidence", "coverage_record"),
            updated("evidence", "fact_snapshot"),
            updated("evidence", "finding"),
            updated("packet", "handoff"),
            updated("packet", "packet_review"),
            updated("packet", "packet_revision"),
            updated("packet", "packet_draft"),
            updated("packet", "scope_recheck"),
            updated("harness", "checkpoint"),
            updated("harness", "plan_revision"),
            updated("harness", "workspace_ref"),
            updated("harness", "session_state"),
            created("task", "process_artifact_file",
                    "EXISTS (SELECT 1 FROM task.process_artifact_batch batch "
                            + "WHERE batch.batch_id = target.batch_id AND batch.created_at < ?)"),
            created("task", "process_artifact_batch"),
            created("task", "task_attachment"),
            created("task", "business_evidence_accum"),
            created("task", "business_decision_packet"),
            created("task", "guidance_draft"),
            updated("task", "tool_invocation"),
            updated("task", "task_lease"),
            updated("task", "step_approval"),
            updated("task", "task_premise"),
            created("task", "task_business_scene_skill"),
            updated("task", "investigation_step"),
            updated("task", "scope_revision"),
            new Table("governance", "audit_event",
                    "(target.updated_at < ? OR EXISTS (SELECT 1 FROM task.investigation_task task "
                            + "WHERE task.task_id = target.task_id AND task.updated_at < ?))", 2),
            updated("task", "investigation_task"),
            updated("governance", "outbox_event"));

    private RetentionCatalog() { }

    static List<Table> tables() { return TABLES; }

    static Instant cutoff(Clock clock) { return clock.instant().minus(30, ChronoUnit.DAYS); }

    private static Table updated(String schema, String name) {
        return new Table(schema, name, "target.updated_at < ?", 1);
    }

    private static Table created(String schema, String name) {
        return new Table(schema, name, "target.created_at < ?", 1);
    }

    private static Table created(String schema, String name, String condition) {
        return new Table(schema, name, condition, 1);
    }

    /** 清理 SQL 仅由该类中的静态标识符组成，截止时间和批次大小仍由参数绑定。 */
    record Table(String schema, String name, String condition, int cutoffParameters) {
        String fullName() { return schema + "." + name; }

        String deleteSql() {
            return "DELETE FROM " + fullName() + " WHERE ctid IN (SELECT target.ctid FROM "
                    + fullName() + " target WHERE " + condition
                    + " LIMIT ? FOR UPDATE OF target SKIP LOCKED)";
        }
    }
}
