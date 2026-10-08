package cn.com.crc.ems.finance.agent.model.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.evidence.application.CoverageRecordRepository;
import cn.com.crc.ems.finance.agent.evidence.application.FactSnapshotRepository;
import cn.com.crc.ems.finance.agent.evidence.application.FindingRepository;
import cn.com.crc.ems.finance.agent.evidence.domain.EffectiveClass;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisContext;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanContext;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanSuggestion;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.ScopeRevisionRepository;

/**
 *
 * @Description: 跨任务、范围、证据与模型端口编排业务模型上下文，不直接修改权威状态。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-06 23:00:00
 */
@Service
public final class BusinessModelInteractionService {

    private final BusinessModelClient modelClient;
    private final InvestigationTaskRepository taskRepository;
    private final ScopeRevisionRepository scopeRepository;
    private final FactSnapshotRepository snapshotRepository;
    private final CoverageRecordRepository coverageRepository;
    private final FindingRepository findingRepository;

    public BusinessModelInteractionService(BusinessModelClient modelClient,
            InvestigationTaskRepository taskRepository, ScopeRevisionRepository scopeRepository,
            FactSnapshotRepository snapshotRepository, CoverageRecordRepository coverageRepository,
            FindingRepository findingRepository) {
        this.modelClient = modelClient;
        this.taskRepository = taskRepository;
        this.scopeRepository = scopeRepository;
        this.snapshotRepository = snapshotRepository;
        this.coverageRepository = coverageRepository;
        this.findingRepository = findingRepository;
    }

    /** 从当前任务和最新已确认范围生成模型调查方案。 */
    public BusinessModelResult<ModelPlanSuggestion> generatePlan(UUID taskId) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("调查任务不存在或已失效"));
        if (task.currentScopeRevisionId() == null) {
            throw new IllegalStateException("请先确认调查范围，再生成模型方案");
        }
        var scope = scopeRepository.findById(task.currentScopeRevisionId())
                .orElseThrow(() -> new IllegalStateException("当前调查范围不存在或已失效"));
        return modelClient.generatePlan(new ModelPlanContext(task.taskId(), task.title(), task.originalQuestion(),
                scope.scopeRevisionId(), scope.systemCode(), scope.objectSelector().canonical().value(),
                scope.effectiveTimeRange().canonical().value(), scope.includedPaths().canonical().value()));
    }

    /** 从当前任务的脱敏证据统计和判断陈述生成模型分析。 */
    public BusinessModelResult<EvidenceAnalysisSuggestion> analyzeEvidence(UUID taskId) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("调查任务不存在或已失效"));
        var snapshots = snapshotRepository.findByTaskId(taskId);
        var coverages = coverageRepository.findByTaskId(taskId);
        var findings = findingRepository.findByTaskId(taskId);
        int current = (int) snapshots.stream().filter(item -> item.effectiveClass() == EffectiveClass.CURRENT_EFFECTIVE).count();
        int future = (int) snapshots.stream().filter(item -> item.effectiveClass() == EffectiveClass.FUTURE_SCHEDULED).count();
        int undetermined = snapshots.size() - current - future;
        var context = new EvidenceAnalysisContext(taskId, task.title(), task.originalQuestion(), current, future,
                undetermined, coverages.stream().map(item -> item.completeness().name()).distinct().toList(),
                findings.stream().map(item -> item.statement()).distinct().toList());
        return modelClient.analyzeEvidence(context);
    }
}
