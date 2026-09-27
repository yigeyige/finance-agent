package cn.com.crc.ems.finance.agent.task.infrastructure;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.shared.infrastructure.PostgresRepositoryExceptionMapper;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskLeaseRepository;
import cn.com.crc.ems.finance.agent.task.domain.TaskLease;

/**
 *
 * @Description: 负责任务行锁与租约领取的 PostgreSQL 写事务边界。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 22:25:00
 */
@Service
public class TransactionalTaskExecutionService {

    private final InvestigationTaskRepository taskRepository;
    private final TaskLeaseRepository leaseRepository;

    public TransactionalTaskExecutionService(InvestigationTaskRepository taskRepository,
            TaskLeaseRepository leaseRepository) {
        this.taskRepository = taskRepository;
        this.leaseRepository = leaseRepository;
    }

    /**
     *
     * @description: 锁定任务后创建活动租约，唯一索引冲突时整笔事务失败。
     *
     * @param: lease 待创建的租约
     * @return: 已成功写入的租约
     * @throws: RepositoryEntityNotFoundException 任务不存在
     */
    @Transactional
    public TaskLease acquireLease(TaskLease lease) {
        if (lease == null) {
            throw new IllegalArgumentException("lease is required");
        }
        taskRepository.findByIdForUpdate(lease.taskId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation task not found"));
        try {
            leaseRepository.acquire(lease);
            return lease;
        } catch (RuntimeException failure) {
            throw PostgresRepositoryExceptionMapper.mapLeaseAcquisition("lease acquisition", failure);
        }
    }
}
