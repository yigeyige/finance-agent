package cn.com.crc.ems.finance.agent.harness.web;

import java.util.List;

import cn.com.crc.ems.finance.agent.harness.application.BusinessModelCallMetadata;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseEvidence;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanSuggestion;

/**
 *
 * @Description: 模型业务建议及安全调用元数据的 REST 响应。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public final class BusinessModelResponse {

    private BusinessModelResponse() {
    }

    public record Metadata(String mode, String modelName, String inputDigest, String outputDigest,
            int tokens, long latencyMillis) {
        static Metadata from(BusinessModelCallMetadata value) {
            return new Metadata(value.mode(), value.modelName(), value.inputDigest(), value.outputDigest(),
                    value.tokens(), value.latencyMillis());
        }
    }

    public record Plan(String recommendedGoal, List<String> alternativeGoals, String reason, Metadata metadata) {
        static Plan from(BusinessModelResult<ModelPlanSuggestion> result) {
            return new Plan(result.value().recommendedGoal(), result.value().alternativeGoals(),
                    result.value().reason(), Metadata.from(result.metadata()));
        }
    }

    public record Guidance(String reply, BusinessHandlingDraftResponse draft, Metadata metadata) {
        static Guidance from(BusinessModelResult<BusinessGuidanceSuggestion> result) {
            var draft = result.value().draft();
            return new Guidance(result.value().reply(), new BusinessHandlingDraftResponse(draft.subjectKey(), draft.statement(), draft.sourceDescription(),
                    draft.systemCode(), draft.objectKey(), draft.startDate(), draft.endDate(),
                    draft.investigationAreas(), draft.dataUse(), draft.recommendedGoal(), draft.alternativeGoals(),
                    draft.reason(), draft.conclusion(), draft.evidence()), Metadata.from(result.metadata()));
        }
    }

    public record BusinessHandlingDraftResponse(String subjectKey,
            String statement, String sourceDescription, String systemCode, String objectKey, String startDate,
            String endDate, List<String> investigationAreas, String dataUse, String recommendedGoal,
            List<String> alternativeGoals, String reason, String conclusion, List<EmsDatabaseEvidence> evidence) {
    }

    public record Analysis(String summary, String riskLevel, String riskExplanation,
            List<String> recommendations, Metadata metadata) {
        static Analysis from(BusinessModelResult<EvidenceAnalysisSuggestion> result) {
            return new Analysis(result.value().summary(), result.value().riskLevel(),
                    result.value().riskExplanation(), result.value().recommendations(),
                    Metadata.from(result.metadata()));
        }
    }

    /** 初稿生成阶段进度，未开始时 stage 为空字符串。 */
    public record Progress(String stage) {
        public static Progress of(String stage) {
            return new Progress(stage == null ? "" : stage);
        }
    }
}
