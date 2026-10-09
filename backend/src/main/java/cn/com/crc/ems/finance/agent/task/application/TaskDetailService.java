package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.ScopeRevision;
import cn.com.crc.ems.finance.agent.task.domain.TaskPremise;

/**
 *
 * @Description: 组装任务列表双击后展示的只读业务处理详情，不返回执行密钥或模型原文。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-09 20:45:00
 */
@Service
public class TaskDetailService {
    private final InvestigationTaskRepository taskRepository;
    private final TaskPremiseRepository premiseRepository;
    private final ScopeRevisionRepository scopeRevisionRepository;
    private final InvestigationStepRepository stepRepository;

    public TaskDetailService(InvestigationTaskRepository taskRepository, TaskPremiseRepository premiseRepository,
            ScopeRevisionRepository scopeRevisionRepository, InvestigationStepRepository stepRepository) {
        this.taskRepository = taskRepository;
        this.premiseRepository = premiseRepository;
        this.scopeRevisionRepository = scopeRevisionRepository;
        this.stepRepository = stepRepository;
    }

    /**
     *
     * @description: 查询当前身份有权查看的任务业务、范围与方案只读详情。
     *
     * @param: taskId 任务标识
     * @param: identity 当前可信身份
     * @return: 任务只读详情
     * @throws: RepositoryEntityNotFoundException 任务不存在或当前身份无权访问时抛出
     * @author: yigeyige99
     */
    @Transactional(readOnly = true)
    public Detail find(UUID taskId, CurrentIdentity identity) {
        InvestigationTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RepositoryEntityNotFoundException("业务处理任务不存在或无权查看"));
        if (!identity.hasRole(RoleCode.PLATFORM_ADMIN) && !task.initiatorUserId().equals(identity.userId())) {
            throw new RepositoryEntityNotFoundException("业务处理任务不存在或无权查看");
        }
        ScopeRevision scope = task.currentScopeRevisionId() == null ? null
                : scopeRevisionRepository.findById(task.currentScopeRevisionId()).orElse(null);
        return new Detail(task.taskId(), task.title(), task.originalQuestion(), task.status().name(), task.createdAt(),
                premiseRepository.findByTaskId(task.taskId()).stream().map(Premise::from).toList(), Scope.from(scope),
                stepRepository.findByTaskId(task.taskId()).stream().map(PlanStep::from).toList());
    }

    public record Detail(UUID taskId, String title, String description, String status, Instant createdAt,
            List<Premise> premises, Scope scope, List<PlanStep> plans) { }
    public record Premise(String premiseType, String subjectKey, String statement, String sourceDescription) {
        static Premise from(TaskPremise value) { return new Premise(value.premiseType(), value.subjectKey(), value.statement(), value.sourceDescription()); }
    }
    public record Scope(String systemCode, String objectSelector, String effectiveTimeRange, int revisionNo) {
        static Scope from(ScopeRevision value) { return value == null ? null : new Scope(value.systemCode(), value.objectSelector().canonical().value(), value.effectiveTimeRange().canonical().value(), value.revisionNo()); }
    }
    public record PlanStep(int sequenceNo, String goal, boolean recommended, String status) {
        static PlanStep from(InvestigationStep value) { return new PlanStep(value.sequenceNo(), value.goal(), value.recommended(), value.status().name()); }
    }
}
