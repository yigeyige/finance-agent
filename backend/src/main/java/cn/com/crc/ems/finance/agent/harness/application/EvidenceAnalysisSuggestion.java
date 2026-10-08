package cn.com.crc.ems.finance.agent.harness.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

/** 严格校验后的模型证据分析，只保留业务摘要、风险与建议。 */
public record EvidenceAnalysisSuggestion(String summary, String riskLevel, String riskExplanation,
        List<String> recommendations) {

    private static final Set<String> FIELDS = Set.of("summary", "riskLevel", "riskExplanation", "recommendations");
    private static final Set<String> RISK_LEVELS = Set.of("LOW", "MEDIUM", "HIGH");

    public EvidenceAnalysisSuggestion {
        requireText(summary, "summary", 4000);
        requireText(riskExplanation, "riskExplanation", 4000);
        if (!RISK_LEVELS.contains(riskLevel) || recommendations == null || recommendations.isEmpty()
                || recommendations.size() > 8 || recommendations.stream()
                        .anyMatch(value -> value == null || value.isBlank() || value.length() > 2000)) {
            throw new IllegalArgumentException("evidence analysis fields are invalid");
        }
        recommendations = List.copyOf(recommendations);
    }

    /** 从模型 JSON 严格读取证据分析并拒绝额外字段。 */
    public static EvidenceAnalysisSuggestion fromJson(JsonNode value) {
        ModelPlanSuggestion.requireExactObject(value, FIELDS);
        JsonNode recommendations = value.get("recommendations");
        if (!recommendations.isArray() || recommendations.isEmpty() || recommendations.size() > 8) {
            throw new BusinessModelException("模型返回的建议列表格式不正确");
        }
        List<String> values = new ArrayList<>();
        recommendations.forEach(item -> values.add(ModelPlanSuggestion.text(item, "recommendation", 2000)));
        try {
            return new EvidenceAnalysisSuggestion(ModelPlanSuggestion.text(value.get("summary"), "summary", 4000),
                    ModelPlanSuggestion.text(value.get("riskLevel"), "riskLevel", 20),
                    ModelPlanSuggestion.text(value.get("riskExplanation"), "riskExplanation", 4000), values);
        } catch (IllegalArgumentException exception) {
            throw new BusinessModelException("模型返回的证据分析格式不正确", exception);
        }
    }

    private static void requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank() || value.length() > maxLength) {
            throw new IllegalArgumentException(field + " is invalid");
        }
    }
}
