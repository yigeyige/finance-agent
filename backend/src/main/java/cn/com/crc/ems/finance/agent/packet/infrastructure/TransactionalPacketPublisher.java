package cn.com.crc.ems.finance.agent.packet.infrastructure;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.governance.application.AuditOutboxTransaction;
import cn.com.crc.ems.finance.agent.packet.application.PacketRevisionRepository;
import cn.com.crc.ems.finance.agent.packet.application.PublishTransactionCommand;
import cn.com.crc.ems.finance.agent.packet.domain.PacketRevision;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PostgresRepositoryExceptionMapper;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryStateViolationException;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/**
 *
 * @Description: 按附件 A 伪代码执行决策包、审计、outbox 和任务状态的原子发布事务。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 22:25:00
 */
@Service
public class TransactionalPacketPublisher {

    private final InvestigationTaskRepository taskRepository;
    private final PacketRevisionRepository revisionRepository;
    private final AuditOutboxTransaction auditOutboxTransaction;

    public TransactionalPacketPublisher(InvestigationTaskRepository taskRepository,
            PacketRevisionRepository revisionRepository, AuditOutboxTransaction auditOutboxTransaction) {
        this.taskRepository = taskRepository;
        this.revisionRepository = revisionRepository;
        this.auditOutboxTransaction = auditOutboxTransaction;
    }

    /**
     *
     * @description: 锁定任务并以固定顺序完成不可变发布写入。
     *
     * @param: command 已完成草稿、复核和范围重查的发布命令
     * @return: 成功写入的不可变发布修订
     * @throws: RepositoryEntityNotFoundException 任务不存在
     * @throws: RepositoryStateViolationException 任务不处于可发布状态
     */
    @Transactional
    public PacketRevision publish(PublishTransactionCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command is required");
        }
        // 发布命令携带不可变 revisionId；提交成功后的重复请求直接重放原修订。
        var existingRevision = revisionRepository.findById(command.revision().packetRevisionId());
        if (existingRevision.isPresent()) {
            return existingRevision.get();
        }
        var task = taskRepository.findByIdForUpdate(command.taskId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation task not found"));
        if (task.status() != TaskStatus.READY_TO_PUBLISH) {
            throw new RepositoryStateViolationException("investigation task is not ready to publish");
        }
        try {
            revisionRepository.insert(command.revision());
            auditOutboxTransaction.append(command.auditEvent(), command.outboxEvent());
            taskRepository.updateWithVersionOrThrow(command.publishedTask(), task.rowVersion());
            return command.revision();
        } catch (RuntimeException failure) {
            throw PostgresRepositoryExceptionMapper.map("publication transaction", failure);
        }
    }
}
