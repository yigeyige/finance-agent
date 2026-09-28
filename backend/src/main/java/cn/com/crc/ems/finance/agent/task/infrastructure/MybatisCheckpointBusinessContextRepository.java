package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import cn.com.crc.ems.finance.agent.harness.application.CheckpointBusinessContextRepository;
import cn.com.crc.ems.finance.agent.task.application.InvestigationStepRepository;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.ScopeRevisionRepository;
import cn.com.crc.ems.finance.agent.task.application.StepApprovalRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.ScopeRevision;
import cn.com.crc.ems.finance.agent.task.domain.StepApproval;

/** 在任务基础设施边界组合 checkpoint 所需的当前业务上下文。 */
@Repository
public class MybatisCheckpointBusinessContextRepository implements CheckpointBusinessContextRepository {

    private final InvestigationTaskRepository taskRepository;
    private final InvestigationStepRepository stepRepository;
    private final ScopeRevisionRepository scopeRevisionRepository;
    private final StepApprovalRepository approvalRepository;

    public MybatisCheckpointBusinessContextRepository(InvestigationTaskRepository taskRepository,
            InvestigationStepRepository stepRepository, ScopeRevisionRepository scopeRevisionRepository,
            StepApprovalRepository approvalRepository) {
        this.taskRepository = taskRepository;
        this.stepRepository = stepRepository;
        this.scopeRevisionRepository = scopeRevisionRepository;
        this.approvalRepository = approvalRepository;
    }

    @Override
    public Optional<CheckpointBusinessContext> find(UUID taskId, UUID stepId, UUID scopeRevisionId, UUID approvalId) {
        Optional<InvestigationTask> task = taskRepository.findById(taskId);
        if (task.isEmpty()) {
            return Optional.empty();
        }
        InvestigationTask currentTask = task.get();
        InvestigationStep step = stepRepository.findById(stepId).orElse(null);
        ScopeRevision scope = scopeRevisionRepository.findById(scopeRevisionId).orElse(null);
        StepApproval approval = approvalRepository.findById(approvalId).orElse(null);
        boolean stepAvailable = step != null && taskId.equals(step.taskId());
        boolean scopeAvailable = scope != null && taskId.equals(scope.taskId());
        boolean approvalAvailable = approval != null && stepId.equals(approval.stepId())
                && scopeRevisionId.equals(approval.scopeRevisionId());
        return Optional.of(new CheckpointBusinessContext(taskId, currentTask.currentStepId(),
                currentTask.currentScopeRevisionId(), currentTask.runtimeBundleId(), stepAvailable, scopeAvailable,
                approvalAvailable, approval != null && approval.status() == cn.com.crc.ems.finance.agent.task.domain.ApprovalStatus.ACTIVE,
                approval == null ? null : approval.expiresAt(), approval == null ? 0 : approval.maxCalls(),
                approval == null ? 0 : approval.usedCalls()));
    }
}
