package cn.com.crc.ems.finance.agent.harness.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.harness.domain.Checkpoint;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;

/** 追加业务安全 checkpoint，并在写入前校验当前任务批准上下文。 */
@Service
public class CheckpointService {

    private final CheckpointRepository checkpointRepository;
    private final SessionStateRepository sessionStateRepository;
    private final CheckpointBusinessContextRepository businessContextRepository;

    public CheckpointService(CheckpointRepository checkpointRepository,
            SessionStateRepository sessionStateRepository, CheckpointBusinessContextRepository businessContextRepository) {
        this.checkpointRepository = checkpointRepository;
        this.sessionStateRepository = sessionStateRepository;
        this.businessContextRepository = businessContextRepository;
    }

    /** 在完整 task/user/session 边界内追加一个安全 checkpoint。 */
    @Transactional
    public Checkpoint record(CheckpointRecordCommand command) {
        sessionStateRepository.findById(command.sessionId(), command.taskId(), command.userId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("harness session not found"));
        var context = businessContextRepository.find(command.taskId(), command.stepId(), command.scopeRevisionId(),
                command.approvalId()).orElseThrow(() -> new RepositoryEntityNotFoundException("task context not found"));
        if (!command.stepId().equals(context.currentStepId())
                || !command.scopeRevisionId().equals(context.currentScopeRevisionId())
                || !command.runtimeBundleId().equals(context.runtimeBundleId()) || !context.stepBindingAvailable()
                || !context.scopeBindingAvailable()) {
            throw new IllegalArgumentException("checkpoint binding is not current");
        }
        if (!context.approvalBindingAvailable()) {
            throw new IllegalArgumentException("checkpoint approval binding is not current");
        }

        Checkpoint checkpoint = new Checkpoint(UuidGenerator.newUuid(), command.sessionId(), command.taskId(),
                command.userId(), command.stepId(), command.scopeRevisionId(), command.approvalId(),
                command.runtimeBundleId(), command.checkpointType().name(), command.stateRef(), command.actorId(),
                command.occurredAt(), command.actorId(), command.occurredAt());
        checkpointRepository.insert(checkpoint);
        return checkpoint;
    }

}
