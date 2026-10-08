package cn.com.crc.ems.finance.agent.harness.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

/** 严格校验后的模型调查方案，限制为一个推荐和最多两个备选。 */
public record ModelPlanSuggestion(String recommendedGoal, List<String> alternativeGoals, String reason) {

    private static final Set<String> FIELDS = Set.of("recommendedGoal", "alternativeGoals", "reason");

    public ModelPlanSuggestion {
        requireText(recommendedGoal, "recommendedGoal", 2000);
        requireText(reason, "reason", 2000);
        if (alternativeGoals == null || alternativeGoals.size() > 2 || alternativeGoals.stream()
                .anyMatch(value -> value == null || value.isBlank() || value.length() > 2000)) {
            throw new IllegalArgumentException("alternativeGoals is invalid");
        }
        alternativeGoals = List.copyOf(alternativeGoals);
    }

    /** 从模型 JSON 严格读取方案并拒绝额外字段。 */
    public static ModelPlanSuggestion fromJson(JsonNode value) {
        requireExactObject(value, FIELDS);
        JsonNode alternatives = value.get("alternativeGoals");
        if (!alternatives.isArray() || alternatives.size() > 2) {
            throw new BusinessModelException("模型返回的调查方案格式不正确");
        }
        List<String> values = new ArrayList<>();
        alternatives.forEach(item -> values.add(text(item, "alternativeGoal", 2000)));
        try {
            return new ModelPlanSuggestion(text(value.get("recommendedGoal"), "recommendedGoal", 2000),
                    values, text(value.get("reason"), "reason", 2000));
        } catch (IllegalArgumentException exception) {
            throw new BusinessModelException("模型返回的调查方案格式不正确", exception);
        }
    }

    static void requireExactObject(JsonNode value, Set<String> fields) {
        if (value == null || !value.isObject() || value.size() != fields.size()) {
            throw new BusinessModelException("模型返回内容不符合约定结构");
        }
        Iterator<String> names = value.fieldNames();
        while (names.hasNext()) {
            if (!fields.contains(names.next())) {
                throw new BusinessModelException("模型返回内容包含未约定字段");
            }
        }
        fields.forEach(field -> {
            if (!value.has(field)) {
                throw new BusinessModelException("模型返回内容缺少必要字段");
            }
        });
    }

    static String text(JsonNode value, String field, int maxLength) {
        if (value == null || !value.isTextual() || value.textValue().isBlank()
                || value.textValue().length() > maxLength) {
            throw new BusinessModelException("模型返回字段格式不正确: " + field);
        }
        return value.textValue();
    }

    private static void requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank() || value.length() > maxLength) {
            throw new IllegalArgumentException(field + " is invalid");
        }
    }
}
