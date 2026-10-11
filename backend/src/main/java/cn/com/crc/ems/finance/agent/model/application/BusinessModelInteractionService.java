package cn.com.crc.ems.finance.agent.model.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.crc.ems.finance.agent.evidence.application.CoverageRecordRepository;
import cn.com.crc.ems.finance.agent.evidence.application.FactSnapshotRepository;
import cn.com.crc.ems.finance.agent.evidence.application.FindingRepository;
import cn.com.crc.ems.finance.agent.evidence.domain.EffectiveClass;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceAttachment;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.BusinessHandlingDraft;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisContext;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanContext;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanSuggestion;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.ScopeRevisionRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskBusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.harness.application.BusinessSceneSkillContext;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseAssistanceService;

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
    private final TaskBusinessSceneSkillRepository taskBusinessSceneSkillRepository;
    private final BusinessSceneSkillRepository businessSceneSkillRepository;
    private final EmsDatabaseAssistanceService emsDatabaseAssistanceService;

    public BusinessModelInteractionService(BusinessModelClient modelClient,
            InvestigationTaskRepository taskRepository, ScopeRevisionRepository scopeRepository,
            FactSnapshotRepository snapshotRepository, CoverageRecordRepository coverageRepository,
            FindingRepository findingRepository) {
        this(modelClient, taskRepository, scopeRepository, snapshotRepository, coverageRepository, findingRepository,
                null, null, null);
    }

    @Autowired
    public BusinessModelInteractionService(BusinessModelClient modelClient,
            InvestigationTaskRepository taskRepository, ScopeRevisionRepository scopeRepository,
            FactSnapshotRepository snapshotRepository, CoverageRecordRepository coverageRepository,
            FindingRepository findingRepository, TaskBusinessSceneSkillRepository taskBusinessSceneSkillRepository,
            BusinessSceneSkillRepository businessSceneSkillRepository, EmsDatabaseAssistanceService emsDatabaseAssistanceService) {
        this.modelClient = modelClient;
        this.taskRepository = taskRepository;
        this.scopeRepository = scopeRepository;
        this.snapshotRepository = snapshotRepository;
        this.coverageRepository = coverageRepository;
        this.findingRepository = findingRepository;
        this.taskBusinessSceneSkillRepository = taskBusinessSceneSkillRepository;
        this.businessSceneSkillRepository = businessSceneSkillRepository;
        this.emsDatabaseAssistanceService = emsDatabaseAssistanceService;
    }

    /**
     *
     * @description: 使用任务权威标题和说明生成或修订未持久化的业务处理草稿。
     *
     * @param: taskId 当前任务标识
     * @param: feedback 管理员本次修改意见，可为空以生成初稿
     * @param: currentDraft 当前页面中的受限草稿，可为空以生成初稿
     * @return: 严格结构化的模型业务处理建议
     * @throws: IllegalStateException 任务不存在时抛出
     * @author: yigeyige99
     */
    public BusinessModelResult<BusinessGuidanceSuggestion> guideBusinessHandling(UUID taskId, String feedback,
            BusinessHandlingDraft currentDraft) {
        return guideBusinessHandling(taskId, feedback, currentDraft, List.of());
    }

    /**
     *
     * @description: 使用任务权威信息和受限附件投影生成初稿，不保存附件内容。
     *
     * @param: taskId 当前任务标识
     * @param: feedback 管理员本次修改意见，可为空
     * @param: currentDraft 当前页面中的受限草稿，可为空
     * @param: attachments 已校验的临时附件文本投影
     * @return: 严格结构化的模型业务处理建议
     * @throws: IllegalStateException 任务不存在时抛出
     * @author: yigeyige99
     */
    public BusinessModelResult<BusinessGuidanceSuggestion> guideBusinessHandling(UUID taskId, String feedback,
            BusinessHandlingDraft currentDraft, List<BusinessGuidanceAttachment> attachments) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("业务处理任务不存在或已失效"));
        BusinessGuidanceContext base = new BusinessGuidanceContext(task.taskId(), task.title(), task.originalQuestion(),
                feedback, currentDraft, findSceneSkill(taskId), attachments);
        return modelClient.guideBusinessHandling(new BusinessGuidanceContext(task.taskId(), task.title(),
                task.originalQuestion(), feedback, currentDraft, findSceneSkill(taskId), attachments,
                emsDatabaseAssistanceService == null ? List.of() : emsDatabaseAssistanceService.collect(modelClient, base)));
    }

    /** 将任务已固化的有效业务场景投影为模型上下文，失效场景拒绝继续生成。 */
    private BusinessSceneSkillContext findSceneSkill(UUID taskId) {
        if (taskBusinessSceneSkillRepository == null || businessSceneSkillRepository == null) {
            return null;
        }
        return taskBusinessSceneSkillRepository.findSkillIdByTaskId(taskId).map(skillId -> businessSceneSkillRepository
                .findActiveById(skillId).map(skill -> new BusinessSceneSkillContext(skill.name(), skill.description(),
                        skill.handlingType(), skill.premiseType()))
                .orElseThrow(() -> new IllegalStateException("任务关联的业务场景 Skill 已失效，请重新创建任务"))).orElse(null);
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
