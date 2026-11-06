package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;
import java.util.Map;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseColumnMetadata;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseTableMetadata;
import cn.com.crc.ems.finance.agent.processartifact.application.ProcessArtifactModelContext;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactPlan;

/**
 *
 * @Description: 业务模型交互端口，隔离供应商 SDK 与任务、证据应用服务。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface BusinessModelClient {

    /** 从候选表目录中选择与业务最相关的表，供后端查询字段后再生成 SQL。 */
    default BusinessModelCall<List<String>> selectRelevantTables(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog) {
        return BusinessModelCall.notCalled(List.of());
    }

    /** 按所选目录路径的上限选表，旧实现仍可使用原有选表入口。 */
    default BusinessModelCall<List<String>> selectRelevantTables(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog, int maxTables) {
        return BusinessModelCall.notCalled(selectRelevantTables(context, tableCatalog).value().stream()
                .limit(maxTables).toList());
    }

    /** 仅生成可选查询计划；真正执行前仍由 EMS SQL 守卫与只读执行器控制。 */
    default BusinessModelCall<EmsDatabaseQueryPlan> planEmsDatabaseQuery(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog, List<EmsDatabaseEvidence> priorEvidence,
            Map<String, List<EmsDatabaseColumnMetadata>> tableColumns) {
        return BusinessModelCall.notCalled(new EmsDatabaseQueryPlan(List.of()));
    }

    /** 把一段自由文本解析为业务处理表单字段，供 AI 预填使用；不依赖任务。 */
    BusinessModelResult<BusinessIntakeSuggestion> extractBusinessIntake(BusinessIntakeContext context);

    /** 按目标生成业务场景提示词草稿；不依赖任务，也不修改场景。 */
    BusinessModelResult<SceneSkillPrefillSuggestion> extractSceneSkillPrefill(SceneSkillPrefillContext context);

    /**
     *
     * @description: 根据业务标题、说明和管理员反馈生成或修订业务处理草稿。
     *
     * @param: context 已受限的业务处理上下文
     * @return: 可回填业务表单的模型建议
     * @author: yigeyige99
     */
    BusinessModelResult<BusinessGuidanceSuggestion> guideBusinessHandling(BusinessGuidanceContext context);

    /** 根据已确认任务范围生成调查方案建议。 */
    BusinessModelResult<ModelPlanSuggestion> generatePlan(ModelPlanContext context);

    /** 根据脱敏证据摘要生成业务分析建议。 */
    BusinessModelResult<EvidenceAnalysisSuggestion> analyzeEvidence(EvidenceAnalysisContext context);

    /**
     *
     * @description: 根据固定任务信息、已有证据和可选决策包提示词生成决策包分析，不启用查询工具。
     *
     * @param: context 已固定的决策包分析输入
     * @return: 含处理对象、结论与建议的模型分析结果
     * @author: yigeyige99
     */
    BusinessModelResult<BusinessPacketAnalysisSuggestion> analyzeDecisionPacket(BusinessPacketAnalysisContext context);

    /** 仅依据指定决策包快照和场景提示词生成受限流程中间表示。 */
    BusinessModelResult<ProcessArtifactPlan> generateProcessArtifacts(ProcessArtifactModelContext context);
}
