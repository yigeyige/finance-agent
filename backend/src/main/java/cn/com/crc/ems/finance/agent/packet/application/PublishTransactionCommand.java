package cn.com.crc.ems.finance.agent.packet.application;

import java.util.Objects;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.governance.domain.AuditEvent;
import cn.com.crc.ems.finance.agent.governance.domain.OutboxEvent;
import cn.com.crc.ems.finance.agent.packet.domain.PacketRevision;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;

/**
 *
 * @Description: 已完成发布前校验、等待单事务落库的发布命令。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record PublishTransactionCommand(UUID taskId, InvestigationTask publishedTask,
        PacketRevision revision, AuditEvent auditEvent, OutboxEvent outboxEvent) {

    public PublishTransactionCommand {
        Objects.requireNonNull(taskId, "taskId");
        Objects.requireNonNull(publishedTask, "publishedTask");
        Objects.requireNonNull(revision, "revision");
        Objects.requireNonNull(auditEvent, "auditEvent");
        Objects.requireNonNull(outboxEvent, "outboxEvent");
        if (!taskId.equals(publishedTask.taskId()) || !taskId.equals(revision.taskId())) {
            throw new IllegalArgumentException("publication objects must belong to the same task");
        }
    }
}
