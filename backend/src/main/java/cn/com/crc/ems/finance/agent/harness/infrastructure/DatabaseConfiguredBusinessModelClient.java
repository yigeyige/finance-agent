package cn.com.crc.ems.finance.agent.harness.infrastructure;

import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisContext;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanContext;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanSuggestion;
import cn.com.crc.ems.finance.agent.modelconfig.application.ModelApiKeyCipher;
import cn.com.crc.ems.finance.agent.modelconfig.application.ModelConnectionConfigService;

/** 每次模型调用读取当前启用的数据库配置，避免模型连接信息留在应用配置文件。 */
public final class DatabaseConfiguredBusinessModelClient implements BusinessModelClient {
    private final ModelConnectionConfigService configService;
    private final ModelApiKeyCipher cipher;
    public DatabaseConfiguredBusinessModelClient(ModelConnectionConfigService configService, ModelApiKeyCipher cipher) { this.configService = configService; this.cipher = cipher; }
    @Override public BusinessModelResult<BusinessGuidanceSuggestion> guideBusinessHandling(BusinessGuidanceContext context) { return live().guideBusinessHandling(context); }
    @Override public BusinessModelResult<ModelPlanSuggestion> generatePlan(ModelPlanContext context) { return live().generatePlan(context); }
    @Override public BusinessModelResult<EvidenceAnalysisSuggestion> analyzeEvidence(EvidenceAnalysisContext context) { return live().analyzeEvidence(context); }
    private DeepSeekBusinessModelClient live() { var config = configService.requireEnabled(); return new DeepSeekBusinessModelClient(new DeepSeekOpenAiAdapter(config.apiUrl(), config.modelName(), config.reasoningEffort(), cipher.decrypt(config.apiKeyCiphertext()))); }
}
