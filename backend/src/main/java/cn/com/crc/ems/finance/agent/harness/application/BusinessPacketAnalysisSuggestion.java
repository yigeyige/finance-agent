package cn.com.crc.ems.finance.agent.harness.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @Description: 严格校验后的决策包模型分析：处理对象、结论、推荐/备选方案与理由。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-28 10:00:00
 */
public record BusinessPacketAnalysisSuggestion(String subjectKey, String conclusion, String recommendedGoal,
        List<String> alternativeGoals, String reason) {

    private static final Set<String> FIELDS = Set.of("subjectKey", "conclusion", "recommendedGoal",
            "alternativeGoals", "reason");

    public BusinessPacketAnalysisSuggestion {
        requireText(subjectKey, "subjectKey", 200);
        requireText(conclusion, "conclusion", 2000);
        requireText(recommendedGoal, "recommendedGoal", 2000);
        requireText(reason, "reason", 2000);
        if (alternativeGoals == null || alternativeGoals.size() > 5 || alternativeGoals.stream()
                .anyMatch(value -> value == null || value.isBlank() || value.length() > 2000)) {
            throw new IllegalArgumentException("alternativeGoals is invalid");
        }
        alternativeGoals = List.copyOf(alternativeGoals);
    }

    /**
     *
     * @description: 从模型 JSON 严格读取决策包分析并拒绝额外字段。
     *
     * @param: value 模型输出中的决策包分析 JSON
     * @return: 可用于保存决策包的受限分析结果
     * @throws: BusinessModelException 输出不符合契约时抛出
     * @author: yigeyige99
     */
    public static BusinessPacketAnalysisSuggestion fromJson(JsonNode value) {
        ModelPlanSuggestion.requireExactObject(value, FIELDS);
        JsonNode alternatives = value.get("alternativeGoals");
        if (!alternatives.isArray() || alternatives.size() > 5) {
            throw new BusinessModelException("模型返回的备选方案格式不正确");
        }
        List<String> values = new ArrayList<>();
        alternatives.forEach(item -> values.add(ModelPlanSuggestion.text(item, "alternativeGoal", 2000)));
        try {
            return new BusinessPacketAnalysisSuggestion(
                    ModelPlanSuggestion.text(value.get("subjectKey"), "subjectKey", 200),
                    ModelPlanSuggestion.text(value.get("conclusion"), "conclusion", 2000),
                    ModelPlanSuggestion.text(value.get("recommendedGoal"), "recommendedGoal", 2000), values,
                    ModelPlanSuggestion.text(value.get("reason"), "reason", 2000));
        } catch (IllegalArgumentException exception) {
            throw new BusinessModelException("模型返回的决策包分析格式不正确", exception);
        }
    }

    private static void requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank() || value.length() > maxLength) {
            throw new IllegalArgumentException(field + " is invalid");
        }
    }
}
