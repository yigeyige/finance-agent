package cn.com.crc.ems.finance.agent.diagnostics;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseColumnMetadata;
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
    private final BusinessModelClient delegate;
    private final LatestExceptionStore store;

    public CapturingBusinessModelClient(BusinessModelClient delegate, LatestExceptionStore store) {
        this.delegate = delegate;
        this.store = store;
    }

    @Override
    public List<String> selectRelevantTables(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog) {
        return capture(() -> delegate.selectRelevantTables(context, tableCatalog));
    }

    @Override
    public List<String> selectRelevantTables(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog, int maxTables) {
        return capture(() -> delegate.selectRelevantTables(context, tableCatalog, maxTables));
    }

    @Override
    public EmsDatabaseQueryPlan planEmsDatabaseQuery(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog, List<EmsDatabaseEvidence> priorEvidence,
            Map<String, List<EmsDatabaseColumnMetadata>> tableColumns) {
        return capture(() -> delegate.planEmsDatabaseQuery(context, tableCatalog, priorEvidence, tableColumns));
    }

    @Override
    public BusinessModelResult<BusinessGuidanceSuggestion> guideBusinessHandling(BusinessGuidanceContext context) {
        return capture(() -> delegate.guideBusinessHandling(context));
    }

    @Override
    public BusinessModelResult<ModelPlanSuggestion> generatePlan(ModelPlanContext context) {
        return capture(() -> delegate.generatePlan(context));
    }

    @Override
    public BusinessModelResult<EvidenceAnalysisSuggestion> analyzeEvidence(EvidenceAnalysisContext context) {
        return capture(() -> delegate.analyzeEvidence(context));
    }

    @Override
    public BusinessModelResult<BusinessPacketAnalysisSuggestion> analyzeDecisionPacket(BusinessPacketAnalysisContext context) {
        return capture(() -> delegate.analyzeDecisionPacket(context));
    }

    @Override
    public BusinessModelResult<ProcessArtifactPlan> generateProcessArtifacts(ProcessArtifactModelContext context) {
        return capture(() -> delegate.generateProcessArtifacts(context));
    }

    /** 只在失败穿出模型端口时记录一次，不改变异常对象和调用结果。 */
    private <T> T capture(Supplier<T> call) {
        try {
            return call.get();
        } catch (RuntimeException exception) {
            store.record(LatestExceptionStore.Source.MODEL_INTERACTION, exception);
            throw exception;
        }
    }
}
