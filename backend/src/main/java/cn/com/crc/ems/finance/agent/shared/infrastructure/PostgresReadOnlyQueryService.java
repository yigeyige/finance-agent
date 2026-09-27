package cn.com.crc.ems.finance.agent.shared.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.packet.application.PacketRevisionRepository;
import cn.com.crc.ems.finance.agent.packet.domain.PacketRevision;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskLeaseRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskLease;

/**
 *
 * @Description: 为跨模块只读查询提供显式 readOnly PostgreSQL 事务入口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 22:25:00
 */
@Service
public class PostgresReadOnlyQueryService {

    private final InvestigationTaskRepository taskRepository;
    private final TaskLeaseRepository leaseRepository;
    private final PacketRevisionRepository revisionRepository;

    public PostgresReadOnlyQueryService(InvestigationTaskRepository taskRepository,
            TaskLeaseRepository leaseRepository, PacketRevisionRepository revisionRepository) {
        this.taskRepository = taskRepository;
        this.leaseRepository = leaseRepository;
        this.revisionRepository = revisionRepository;
    }

    /** 在只读事务中查询任务。 */
    @Transactional(readOnly = true)
    public Optional<InvestigationTask> findTask(UUID taskId) {
        return taskRepository.findById(taskId);
    }

    /** 在只读事务中查询活动租约。 */
    @Transactional(readOnly = true)
    public Optional<TaskLease> findActiveLease(UUID taskId) {
        return leaseRepository.findActiveByTaskId(taskId);
    }

    /** 在只读事务中查询已发布修订。 */
    @Transactional(readOnly = true)
    public Optional<PacketRevision> findPublishedRevision(UUID taskId) {
        return revisionRepository.findPublishedByTaskId(taskId);
    }
}
