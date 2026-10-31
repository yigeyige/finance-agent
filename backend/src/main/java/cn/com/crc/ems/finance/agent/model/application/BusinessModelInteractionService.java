package cn.com.crc.ems.finance.agent.model.application;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.type.TypeReference;

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
import cn.com.crc.ems.finance.agent.scenario.application.SceneSkillAttachmentRepository;
import cn.com.crc.ems.finance.agent.scenario.domain.SceneSkillAttachment;
import cn.com.crc.ems.finance.agent.harness.application.BusinessSceneSkillContext;
import cn.com.crc.ems.finance.agent.harness.application.SceneSkillAttachmentSummary;
import cn.com.crc.ems.finance.agent.harness.application.SceneSkillAttachmentBody;
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
    private static final String PROCESS_SCENE_SKILL_ID = "a6a050ae-b699-4ca0-af72-4381e990eda0";
    private final cn.com.crc.ems.finance.agent.scenario.application.BpmnStructureProjector bpmnStructureProjector = new cn.com.crc.ems.finance.agent.scenario.application.BpmnStructureProjector();

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
    private final SceneSkillAttachmentRepository sceneSkillAttachmentRepository;

    public BusinessModelInteractionService(BusinessModelClient modelClient,
            InvestigationTaskRepository taskRepository, ScopeRevisionRepository scopeRepository,
            FactSnapshotRepository snapshotRepository, CoverageRecordRepository coverageRepository,
            FindingRepository findingRepository) {
        this(modelClient, taskRepository, scopeRepository, snapshotRepository, coverageRepository, findingRepository,
                null, null, null, null, null, null, null);
    }

    @Autowired
    public BusinessModelInteractionService(BusinessModelClient modelClient,
            InvestigationTaskRepository taskRepository, ScopeRevisionRepository scopeRepository,
            FactSnapshotRepository snapshotRepository, CoverageRecordRepository coverageRepository,
            FindingRepository findingRepository, TaskBusinessSceneSkillRepository taskBusinessSceneSkillRepository,
            BusinessSceneSkillRepository businessSceneSkillRepository, EmsDatabaseAssistanceService emsDatabaseAssistanceService,
            GuidanceDraftRepository guidanceDraftRepository, GuidanceProgressStore progressStore,
            BusinessEvidenceAccumRepository evidenceAccumRepository,
            SceneSkillAttachmentRepository sceneSkillAttachmentRepository) {
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
        this.sceneSkillAttachmentRepository = sceneSkillAttachmentRepository;
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
        List<SceneSkillAttachmentSummary> attachmentCatalog = findSceneAttachmentCatalog(taskId);
        List<EmsDatabaseEvidence> previousEvidence = latestEvidence(task.taskId());
        List<SceneSkillAttachmentBody> requiredBodies = requiredAttachmentBodies(taskId);
        BusinessGuidanceContext base = new BusinessGuidanceContext(task.taskId(), task.title(), task.originalQuestion(),
                feedback, currentDraft, findSceneSkill(taskId), attachments, previousEvidence, attachmentCatalog, requiredBodies);
        List<EmsDatabaseEvidence> collected = emsDatabaseAssistanceService == null ? List.of()
                : emsDatabaseAssistanceService.collect(modelClient, base, task.databaseConfigId(), progress);
        List<EmsDatabaseEvidence> evidence = mergeEvidence(previousEvidence, collected);
        progress.accept("正在生成业务处理初稿");
        BusinessModelResult<BusinessGuidanceSuggestion> result = modelClient.guideBusinessHandling(
                new BusinessGuidanceContext(task.taskId(), task.title(), task.originalQuestion(), feedback, currentDraft,
                        findSceneSkill(taskId), attachments, evidence, attachmentCatalog, requiredBodies));
        List<SceneSkillAttachmentBody> bodies = mergeBodies(requiredBodies,
                resolveRequestedBodies(taskId, result.value().requestedAttachments()));
        if (!bodies.isEmpty()) {
            progress.accept("正在结合场景附件补充分析");
            result = modelClient.guideBusinessHandling(
                    new BusinessGuidanceContext(task.taskId(), task.title(), task.originalQuestion(), feedback, currentDraft,
                            findSceneSkill(taskId), attachments, evidence, attachmentCatalog, bodies));
        }
        if (isProcessScene(taskId) && result.value().draft().processPlan() == null) {
            result = result.withWarning("模型未形成可用的流程结构，请补充说明后重新分析");
        }
        progress.accept("初稿生成完成");
        if (guidanceDraftRepository != null) {
            guidanceDraftRepository.save(taskId, result.value().draft().toJson().toString(), task.initiatorUserId());
        }
        if (!collected.isEmpty()) {
            accumulateEvidence(task.taskId(), evidence);
        }
        return result;
    }

    /** 读取任务关联场景的附件清单（文件名+摘要），供模型第一轮判断是否需要正文。 */
    private List<SceneSkillAttachmentSummary> findSceneAttachmentCatalog(UUID taskId) {
        if (sceneSkillAttachmentRepository == null || taskBusinessSceneSkillRepository == null) {
            return List.of();
        }
        return taskBusinessSceneSkillRepository.findSkillIdByTaskId(taskId)
                .map(sceneSkillAttachmentRepository::findBySkillId).orElse(List.of()).stream()
                .map(item -> new SceneSkillAttachmentSummary(item.fileName(), item.summary())).toList();
    }

    /** 读取场景必读 XML 附件的结构投影，供第一轮自动注入。 */
    private List<SceneSkillAttachmentBody> requiredAttachmentBodies(UUID taskId) {
        if (sceneSkillAttachmentRepository == null || taskBusinessSceneSkillRepository == null) {
            return List.of();
        }
        return taskBusinessSceneSkillRepository.findSkillIdByTaskId(taskId)
                .map(sceneSkillAttachmentRepository::findBySkillId)
                .orElse(List.of()).stream()
                .filter(item -> item.fileName().toLowerCase(Locale.ROOT).endsWith(".xml"))
                .limit(1)
                .map(item -> {
                    try {
                        return new SceneSkillAttachmentBody(item.fileName() + "（结构投影）",
                                bpmnStructureProjector.project(item.contentText()));
                    } catch (RuntimeException exception) {
                        return new SceneSkillAttachmentBody(item.fileName(), item.summary());
                    }
                })
                .toList();
    }

    /** 按模型第一轮声明的文件名读取附件正文，供第二轮按需注入。 */
    private List<SceneSkillAttachmentBody> resolveRequestedBodies(UUID taskId, List<String> requestedFileNames) {
        if (sceneSkillAttachmentRepository == null || taskBusinessSceneSkillRepository == null
                || requestedFileNames == null || requestedFileNames.isEmpty()) {
            return List.of();
        }
        return taskBusinessSceneSkillRepository.findSkillIdByTaskId(taskId)
                .map(skillId -> sceneSkillAttachmentRepository.findBySkillIdAndFileNames(skillId, requestedFileNames))
                .orElse(List.of()).stream()
                .map(item -> new SceneSkillAttachmentBody(item.fileName(), item.contentText())).toList();
    }

    /** 读取任务最新一轮证据，作为本轮分析的上一轮输入。 */
    private List<EmsDatabaseEvidence> latestEvidence(UUID taskId) {
        if (evidenceAccumRepository == null) {
            return List.of();
        }
        List<BusinessEvidenceAccum> rounds = evidenceAccumRepository.findByTaskIdOrderByRound(taskId);
        if (rounds.isEmpty()) {
            return List.of();
        }
        return limitContextEvidence(parseEvidence(rounds.get(rounds.size() - 1).evidenceJson()));
    }

    /** 合并上一轮与本轮证据，按 schema/table/purpose 去重。 */
    private static List<EmsDatabaseEvidence> mergeEvidence(List<EmsDatabaseEvidence> previous,
            List<EmsDatabaseEvidence> current) {
        LinkedHashMap<String, EmsDatabaseEvidence> merged = new LinkedHashMap<>();
        if (previous != null) {
            previous.forEach(item -> merged.put(evidenceKey(item), item));
        }
        if (current != null) {
            current.forEach(item -> merged.put(evidenceKey(item), item));
        }
        return limitContextEvidence(List.copyOf(merged.values()));
    }

    /** 合并必读附件与模型请求附件，按文件名去重，必读附件优先。 */
    private static List<SceneSkillAttachmentBody> mergeBodies(List<SceneSkillAttachmentBody> required,
            List<SceneSkillAttachmentBody> requested) {
        LinkedHashMap<String, SceneSkillAttachmentBody> merged = new LinkedHashMap<>();
        if (required != null) {
            required.forEach(item -> merged.put(item.fileName(), item));
        }
        if (requested != null) {
            requested.forEach(item -> merged.putIfAbsent(item.fileName(), item));
        }
        return List.copyOf(merged.values());
    }

    private static List<EmsDatabaseEvidence> limitContextEvidence(List<EmsDatabaseEvidence> evidence) {
        if (evidence.size() > 10) {
            return List.copyOf(evidence.subList(evidence.size() - 10, evidence.size()));
        }
        return List.copyOf(evidence);
    }

    private static String evidenceKey(EmsDatabaseEvidence evidence) {
        return evidence.schema() + "\u0000" + evidence.table() + "\u0000" + evidence.purpose();
    }

    private static List<EmsDatabaseEvidence> parseEvidence(String evidenceJson) {
        try {
            return JsonCodec.mapper().readValue(evidenceJson, new TypeReference<List<EmsDatabaseEvidence>>() { });
        } catch (Exception exception) {
            throw new IllegalStateException("证据工作区数据不可解析", exception);
        }
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
                        serializeEvidence(evidence), true, Instant.now()));
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

    /** 判断任务是否关联审批流程生成场景。 */
    private boolean isProcessScene(UUID taskId) {
        return taskBusinessSceneSkillRepository != null
                && taskBusinessSceneSkillRepository.findSkillIdByTaskId(taskId)
                        .filter(id -> PROCESS_SCENE_SKILL_ID.equals(id.toString()))
                        .isPresent();
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
                scope.scopeRevisionId(), scope.objectSelector().canonical().value()));
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
