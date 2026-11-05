package cn.com.crc.ems.finance.agent.diagnostics;

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
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceSuggestion;
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

/**
 * @Description: 在统一模型端口外只记录最终抛出的异常，内部已恢复的失败不产生快照。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-02 23:00:00
 */
public final class CapturingBusinessModelClient implements BusinessModelClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CapturingBusinessModelClient.class);
    private final BusinessModelClient delegate;
    private final LatestExceptionStore store;
    private final InvestigationTaskRepository tasks;

    public CapturingBusinessModelClient(BusinessModelClient delegate, LatestExceptionStore store) {
        this(delegate, store, null);
    }

    public CapturingBusinessModelClient(BusinessModelClient delegate, LatestExceptionStore store,
            InvestigationTaskRepository tasks) {
        this.delegate = delegate;
        this.store = store;
        this.tasks = tasks;
    }

    @Override
    public List<String> selectRelevantTables(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog) {
        return capture(context == null ? null : context.taskId(), () -> delegate.selectRelevantTables(context, tableCatalog));
    }

    @Override
    public List<String> selectRelevantTables(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog, int maxTables) {
        return capture(context == null ? null : context.taskId(), () -> delegate.selectRelevantTables(context, tableCatalog, maxTables));
    }

    @Override
    public EmsDatabaseQueryPlan planEmsDatabaseQuery(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog, List<EmsDatabaseEvidence> priorEvidence,
            Map<String, List<EmsDatabaseColumnMetadata>> tableColumns) {
        return capture(context == null ? null : context.taskId(), () -> delegate.planEmsDatabaseQuery(context, tableCatalog, priorEvidence, tableColumns));
    }

    @Override
    public BusinessModelResult<BusinessGuidanceSuggestion> guideBusinessHandling(BusinessGuidanceContext context) {
        return capture(context == null ? null : context.taskId(), () -> delegate.guideBusinessHandling(context));
    }

    @Override
    public BusinessModelResult<ModelPlanSuggestion> generatePlan(ModelPlanContext context) {
        return capture(context == null ? null : context.taskId(), () -> delegate.generatePlan(context));
    }

    @Override
    public BusinessModelResult<EvidenceAnalysisSuggestion> analyzeEvidence(EvidenceAnalysisContext context) {
        return capture(context == null ? null : context.taskId(), () -> delegate.analyzeEvidence(context));
    }

    @Override
    public BusinessModelResult<BusinessPacketAnalysisSuggestion> analyzeDecisionPacket(BusinessPacketAnalysisContext context) {
        return capture(context == null ? null : context.taskId(), () -> delegate.analyzeDecisionPacket(context));
    }

    @Override
    public BusinessModelResult<ProcessArtifactPlan> generateProcessArtifacts(ProcessArtifactModelContext context) {
        return capture(context == null ? null : context.taskId(), () -> delegate.generateProcessArtifacts(context));
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
