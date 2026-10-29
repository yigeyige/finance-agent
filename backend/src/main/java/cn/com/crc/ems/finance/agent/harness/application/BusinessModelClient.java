package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseColumnMetadata;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseTableMetadata;

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
    default List<String> selectRelevantTables(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog) {
        return List.of();
    }

    /** 仅生成可选查询建议；真正执行前仍由 EMS SQL 守卫与只读执行器控制。 */
    default Optional<EmsDatabaseQueryRequest> planEmsDatabaseQuery(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog, List<EmsDatabaseEvidence> priorEvidence,
            Map<String, List<EmsDatabaseColumnMetadata>> tableColumns) {
        return Optional.empty();
    }

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
}
