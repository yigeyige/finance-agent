package cn.com.crc.ems.finance.agent.harness.application;

/** 业务模型交互端口，隔离供应商 SDK 与任务、证据应用服务。 */
public interface BusinessModelClient {

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
}
