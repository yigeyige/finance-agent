package cn.com.crc.ems.finance.agent.harness.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.harness.application.BusinessPacketAnalysisContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessPacketAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisContext;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseEvidence;
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseQueryRequest;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanContext;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.PromptTemplateRepository;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseColumnMetadata;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseTableMetadata;
import cn.com.crc.ems.finance.agent.processartifact.application.ProcessArtifactModelContext;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactPlan;
import cn.com.crc.ems.finance.agent.modelconfig.application.ModelApiKeyCipher;
import cn.com.crc.ems.finance.agent.modelconfig.application.ModelConnectionConfigService;

/**
 *
 * @Description: 每次模型调用读取当前启用的数据库配置，避免模型连接信息留在应用配置文件。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public final class DatabaseConfiguredBusinessModelClient implements BusinessModelClient {
    private final ModelConnectionConfigService configService;
    private final ModelApiKeyCipher cipher;
    private final PromptTemplateRepository promptTemplates;
    public DatabaseConfiguredBusinessModelClient(ModelConnectionConfigService configService, ModelApiKeyCipher cipher,
            PromptTemplateRepository promptTemplates) {
        this.configService = configService;
        this.cipher = cipher;
        this.promptTemplates = promptTemplates;
    }
    @Override public List<String> selectRelevantTables(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog) { return live().selectRelevantTables(context, tableCatalog); }

    @Override public Optional<EmsDatabaseQueryRequest> planEmsDatabaseQuery(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog, List<EmsDatabaseEvidence> priorEvidence,
            Map<String, List<EmsDatabaseColumnMetadata>> tableColumns) { return live().planEmsDatabaseQuery(context, tableCatalog, priorEvidence, tableColumns); }
    @Override public BusinessModelResult<BusinessGuidanceSuggestion> guideBusinessHandling(BusinessGuidanceContext context) { return live().guideBusinessHandling(context); }
    @Override public BusinessModelResult<ModelPlanSuggestion> generatePlan(ModelPlanContext context) { return live().generatePlan(context); }
    @Override public BusinessModelResult<EvidenceAnalysisSuggestion> analyzeEvidence(EvidenceAnalysisContext context) { return live().analyzeEvidence(context); }
    @Override public BusinessModelResult<BusinessPacketAnalysisSuggestion> analyzeDecisionPacket(BusinessPacketAnalysisContext context) { return live().analyzeDecisionPacket(context); }
    @Override public BusinessModelResult<ProcessArtifactPlan> generateProcessArtifacts(ProcessArtifactModelContext context) { return live().generateProcessArtifacts(context); }
    private DeepSeekBusinessModelClient live() { var config = configService.requireEnabled(); return new DeepSeekBusinessModelClient(new DeepSeekOpenAiAdapter(config.apiUrl(), config.modelName(), config.reasoningEffort(), cipher.decrypt(config.apiKeyCiphertext())), promptTemplates); }
}
