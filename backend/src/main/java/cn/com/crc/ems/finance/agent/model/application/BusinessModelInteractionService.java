package cn.com.crc.ems.finance.agent.model.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

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
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseEvidence;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisContext;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanContext;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanSuggestion;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.GuidanceDraftRepository;
import cn.com.crc.ems.finance.agent.task.application.ScopeRevisionRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskBusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.harness.application.BusinessSceneSkillContext;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseAssistanceService;
import cn.com.crc.ems.finance.agent.businesspacket.application.BusinessEvidenceAccumRepository;
import cn.com.crc.ems.finance.agent.businesspacket.domain.BusinessEvidenceAccum;
import cn.com.crc.ems.finance.agent.shared.domain.JsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;
import org.springframework.dao.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessModelInteractionService.class);

    private final BusinessModelClient modelClient;
    private final InvestigationTaskRepository taskRepository;
    private final ScopeRevisionRepository scopeRepository;
    private final FactSnapshotRepository snapshotRepository;
    private final CoverageRecordRepository coverageRepository;
    private final FindingRepository findingRepository;
    private final TaskBusinessSceneSkillRepository taskBusinessSceneSkillRepository;
    private final BusinessSceneSkillRepository businessSceneSkillRepository;
    private final EmsDatabaseAssistanceService emsDatabaseAssistanceService;
    private final GuidanceDraftRepository guidanceDraftRepository;
    private final GuidanceProgressStore progressStore;
    private final BusinessEvidenceAccumRepository evidenceAccumRepository;

    public BusinessModelInteractionService(BusinessModelClient modelClient,
            InvestigationTaskRepository taskRepository, ScopeRevisionRepository scopeRepository,
            FactSnapshotRepository snapshotRepository, CoverageRecordRepository coverageRepository,
            FindingRepository findingRepository) {
        this(modelClient, taskRepository, scopeRepository, snapshotRepository, coverageRepository, findingRepository,
                null, null, null, null, null, null);
    }

    @Autowired
    public BusinessModelInteractionService(BusinessModelClient modelClient,
            InvestigationTaskRepository taskRepository, ScopeRevisionRepository scopeRepository,
            FactSnapshotRepository snapshotRepository, CoverageRecordRepository coverageRepository,
            FindingRepository findingRepository, TaskBusinessSceneSkillRepository taskBusinessSceneSkillRepository,
            BusinessSceneSkillRepository businessSceneSkillRepository, EmsDatabaseAssistanceService emsDatabaseAssistanceService,
            GuidanceDraftRepository guidanceDraftRepository, GuidanceProgressStore progressStore,
            BusinessEvidenceAccumRepository evidenceAccumRepository) {
        this.modelClient = modelClient;
        this.taskRepository = taskRepository;
        this.scopeRepository = scopeRepository;
        this.snapshotRepository = snapshotRepository;
        this.coverageRepository = coverageRepository;
        this.findingRepository = findingRepository;
        this.taskBusinessSceneSkillRepository = taskBusinessSceneSkillRepository;
        this.businessSceneSkillRepository = businessSceneSkillRepository;
        this.emsDatabaseAssistanceService = emsDatabaseAssistanceService;
        this.guidanceDraftRepository = guidanceDraftRepository;
        this.progressStore = progressStore;
        this.evidenceAccumRepository = evidenceAccumRepository;
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
        Consumer<String> progress = progressStore == null ? stage -> { } : stage -> progressStore.record(task.taskId(), stage);
        progress.accept("正在分析业务说明与处理范围");
        BusinessGuidanceContext base = new BusinessGuidanceContext(task.taskId(), task.title(), task.originalQuestion(),
                feedback, currentDraft, findSceneSkill(taskId), attachments);
        List<EmsDatabaseEvidence> evidence = emsDatabaseAssistanceService == null ? List.of()
                : emsDatabaseAssistanceService.collect(modelClient, base, task.databaseConfigId(), progress);
        progress.accept("正在生成业务处理初稿");
        BusinessModelResult<BusinessGuidanceSuggestion> result = modelClient.guideBusinessHandling(
                new BusinessGuidanceContext(task.taskId(), task.title(), task.originalQuestion(), feedback, currentDraft,
                        findSceneSkill(taskId), attachments, evidence));
        progress.accept("初稿生成完成");
        if (guidanceDraftRepository != null) {
            guidanceDraftRepository.save(taskId, result.value().draft().toJson().toString(), task.initiatorUserId());
        }
        accumulateEvidence(task.taskId(), evidence);
        return result;
    }

    /** 将当轮非空证据追加写入证据工作区，保留轮次且不覆盖历史轮次；唯一约束冲突时重新分配轮次。 */
    private void accumulateEvidence(UUID taskId, List<EmsDatabaseEvidence> evidence) {
        if (evidenceAccumRepository == null || evidence == null || evidence.isEmpty()) {
            return;
        }
        for (int attempt = 1; attempt <= 3; attempt++) {
            int nextRound = evidenceAccumRepository.findMaxRoundNo(taskId) + 1;
            try {
                evidenceAccumRepository.save(new BusinessEvidenceAccum(UuidGenerator.newUuid(), taskId, nextRound,
                        serializeEvidence(evidence), Instant.now()));
                return;
            } catch (DuplicateKeyException exception) {
                if (attempt == 3) {
                    LOGGER.warn("证据轮次分配冲突，放弃本轮证据累积 taskId={}", taskId, exception);
                }
            }
        }
    }

    private static String serializeEvidence(List<EmsDatabaseEvidence> evidence) {
        try {
            return JsonCodec.mapper().writeValueAsString(evidence);
        } catch (Exception exception) {
            throw new IllegalStateException("关联证据序列化失败", exception);
        }
    }

    /** 将任务已固化的有效业务场景投影为模型上下文，失效场景拒绝继续生成。 */
    private BusinessSceneSkillContext findSceneSkill(UUID taskId) {
        if (taskBusinessSceneSkillRepository == null || businessSceneSkillRepository == null) {
            return null;
        }
        return taskBusinessSceneSkillRepository.findSkillIdByTaskId(taskId).map(skillId -> businessSceneSkillRepository
                .findActiveById(skillId).map(skill -> new BusinessSceneSkillContext(skill.name(), skill.investigationPrompt(),
                        skill.decisionPacketPrompt()))
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
