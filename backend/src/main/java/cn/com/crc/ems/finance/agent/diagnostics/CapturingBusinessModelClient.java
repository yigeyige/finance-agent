package cn.com.crc.ems.finance.agent.diagnostics;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseColumnMetadata;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseTableMetadata;
import cn.com.crc.ems.finance.agent.harness.application.BusinessIntakeContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessIntakeSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelCall;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelCallMetadata;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.harness.application.BusinessPacketAnalysisContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessPacketAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseEvidence;
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseQueryPlan;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisContext;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanContext;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanSuggestion;
import cn.com.crc.ems.finance.agent.processartifact.application.ProcessArtifactModelContext;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactPlan;
import cn.com.crc.ems.finance.agent.usage.application.TokenUsageRecorder;

/**
 * @Description: 在统一模型端口外记录最终抛出异常的快照，并把真实调用的 token 用量交给用量端口。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-02 23:00:00
 */
public final class CapturingBusinessModelClient implements BusinessModelClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CapturingBusinessModelClient.class);
    private static final String LIVE_MODE = "LIVE";
    private final BusinessModelClient delegate;
    private final LatestExceptionStore store;
    private final InvestigationTaskRepository tasks;
    private final TokenUsageRecorder usageRecorder;

    public CapturingBusinessModelClient(BusinessModelClient delegate, LatestExceptionStore store) {
        this(delegate, store, null, null);
    }

    public CapturingBusinessModelClient(BusinessModelClient delegate, LatestExceptionStore store,
            InvestigationTaskRepository tasks) {
        this(delegate, store, tasks, null);
    }

    public CapturingBusinessModelClient(BusinessModelClient delegate, LatestExceptionStore store,
            InvestigationTaskRepository tasks, TokenUsageRecorder usageRecorder) {
        this.delegate = delegate;
        this.store = store;
        this.tasks = tasks;
        this.usageRecorder = usageRecorder;
    }

    @Override
    public BusinessModelCall<List<String>> selectRelevantTables(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog) {
        UUID taskId = context == null ? null : context.taskId();
        BusinessModelCall<List<String>> result = capture(taskId,
                () -> delegate.selectRelevantTables(context, tableCatalog));
        recordUsage(taskId, result.metadata());
        return result;
    }

    @Override
    public BusinessModelCall<List<String>> selectRelevantTables(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog, int maxTables) {
        UUID taskId = context == null ? null : context.taskId();
        BusinessModelCall<List<String>> result = capture(taskId,
                () -> delegate.selectRelevantTables(context, tableCatalog, maxTables));
        recordUsage(taskId, result.metadata());
        return result;
    }

    @Override
    public BusinessModelCall<EmsDatabaseQueryPlan> planEmsDatabaseQuery(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog, List<EmsDatabaseEvidence> priorEvidence,
            Map<String, List<EmsDatabaseColumnMetadata>> tableColumns) {
        UUID taskId = context == null ? null : context.taskId();
        BusinessModelCall<EmsDatabaseQueryPlan> result = capture(taskId,
                () -> delegate.planEmsDatabaseQuery(context, tableCatalog, priorEvidence, tableColumns));
        recordUsage(taskId, result.metadata());
        return result;
    }

    @Override
    public BusinessModelResult<BusinessIntakeSuggestion> extractBusinessIntake(BusinessIntakeContext context) {
        BusinessModelResult<BusinessIntakeSuggestion> result = capture(null,
                () -> delegate.extractBusinessIntake(context));
        recordUsage(null, result.metadata());
        return result;
    }

    @Override
    public BusinessModelResult<BusinessGuidanceSuggestion> guideBusinessHandling(BusinessGuidanceContext context) {
        UUID taskId = context == null ? null : context.taskId();
        BusinessModelResult<BusinessGuidanceSuggestion> result = capture(taskId,
                () -> delegate.guideBusinessHandling(context));
        recordUsage(taskId, result.metadata());
        return result;
    }

    @Override
    public BusinessModelResult<ModelPlanSuggestion> generatePlan(ModelPlanContext context) {
        UUID taskId = context == null ? null : context.taskId();
        BusinessModelResult<ModelPlanSuggestion> result = capture(taskId, () -> delegate.generatePlan(context));
        recordUsage(taskId, result.metadata());
        return result;
    }

    @Override
    public BusinessModelResult<EvidenceAnalysisSuggestion> analyzeEvidence(EvidenceAnalysisContext context) {
        UUID taskId = context == null ? null : context.taskId();
        BusinessModelResult<EvidenceAnalysisSuggestion> result = capture(taskId,
                () -> delegate.analyzeEvidence(context));
        recordUsage(taskId, result.metadata());
        return result;
    }

    @Override
    public BusinessModelResult<BusinessPacketAnalysisSuggestion> analyzeDecisionPacket(BusinessPacketAnalysisContext context) {
        UUID taskId = context == null ? null : context.taskId();
        BusinessModelResult<BusinessPacketAnalysisSuggestion> result = capture(taskId,
                () -> delegate.analyzeDecisionPacket(context));
        recordUsage(taskId, result.metadata());
        return result;
    }

    @Override
    public BusinessModelResult<ProcessArtifactPlan> generateProcessArtifacts(ProcessArtifactModelContext context) {
        UUID taskId = context == null ? null : context.taskId();
        BusinessModelResult<ProcessArtifactPlan> result = capture(taskId,
                () -> delegate.generateProcessArtifacts(context));
        recordUsage(taskId, result.metadata());
        return result;
    }

    /** 只在失败穿出模型端口时记录一次，不改变异常对象和调用结果。 */
    private <T> T capture(UUID taskId, Supplier<T> call) {
        try {
            return call.get();
        } catch (RuntimeException exception) {
            var identity = IdentityContext.current();
            UUID owner = identity == null ? taskOwner(taskId) : identity.userId();
            store.record(LatestExceptionStore.Source.MODEL_INTERACTION, exception,
                    owner);
            throw exception;
        }
    }

    /**
     * @description: 记录一次真实调用的 token 用量；归属优先当前身份，后台线程回落任务发起人。
     * @param: taskId 本次调用归属任务，可为空
     * @param: metadata 模型返回的调用元数据
     * @author: yigeyige99
     */
    private void recordUsage(UUID taskId, BusinessModelCallMetadata metadata) {
        if (usageRecorder == null || metadata == null) return;
        if (!LIVE_MODE.equals(metadata.mode()) || metadata.tokens() <= 0) return;
        var identity = IdentityContext.current();
        UUID owner = identity == null ? taskOwner(taskId) : identity.userId();
        if (owner == null) {
            LOGGER.warn("模型 token 用量无法归属用户，已跳过本次记录");
            return;
        }
        usageRecorder.record(owner, taskId, metadata.modelName(), metadata.tokens(), Instant.now());
    }

    /** 后台模型调用无请求线程身份时，从可信任务发起人解析归属。 */
    private UUID taskOwner(UUID taskId) {
        if (taskId == null || tasks == null) return null;
        try {
            return tasks.findById(taskId).map(task -> task.initiatorUserId()).orElse(null);
        } catch (RuntimeException lookupFailure) {
            LOGGER.warn("模型异常无法关联任务发起人，快照仅供管理员查看");
            return null;
        }
    }
}
