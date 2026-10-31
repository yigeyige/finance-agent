package cn.com.crc.ems.finance.agent.harness.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactPlan;

/**
 *
 * @Description: 业务处理向导中由模型返回且可由管理员修订的受限结构化草稿；流程场景可附带结构化结论。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-08 21:10:00
 */
public record BusinessHandlingDraft(String subjectKey, String objectKey, String recommendedGoal,
        List<String> alternativeGoals, String reason, String conclusion, List<EmsDatabaseEvidence> evidence,
        ProcessArtifactPlan processPlan) {

    private static final Set<String> FIELDS = Set.of("subjectKey", "objectKey",
            "recommendedGoal", "alternativeGoals", "reason", "conclusion", "evidence");

    public BusinessHandlingDraft(String subjectKey, String objectKey, String recommendedGoal,
            List<String> alternativeGoals, String reason, String conclusion, List<EmsDatabaseEvidence> evidence) {
        this(subjectKey, objectKey, recommendedGoal, alternativeGoals, reason, conclusion, evidence, null);
    }

    public BusinessHandlingDraft {
        requireText(subjectKey, "subjectKey", 200);
        requireText(objectKey, "objectKey", 200);
        requireText(recommendedGoal, "recommendedGoal", 2000);
        if (alternativeGoals == null || alternativeGoals.size() > 5) {
            throw new IllegalArgumentException("alternativeGoals is invalid");
        }
        alternativeGoals = List.copyOf(alternativeGoals);
        alternativeGoals.forEach(value -> requireText(value, "alternativeGoal", 2000));
        requireText(reason, "reason", 2000);
        conclusion = conclusion == null ? "" : conclusion;
        if (conclusion.length() > 2000) {
            throw new IllegalArgumentException("conclusion is invalid");
        }
        evidence = evidence == null ? List.of() : List.copyOf(evidence);
        if (evidence.size() > 10) {
            throw new IllegalArgumentException("evidence is invalid");
        }
    }

    /**
     *
     * @description: 严格解析模型返回的业务处理草稿，拒绝额外字段和越界枚举。
     *
     * @param: value 模型输出中的草稿 JSON
     * @return: 可用于业务表单的受限草稿
     * @throws: BusinessModelException 输出不符合契约时抛出
     * @author: yigeyige99
     */
    public static BusinessHandlingDraft fromJson(JsonNode value) {
        requireExactObject(value);
        List<String> alternatives = textArray(value.get("alternativeGoals"), "alternativeGoals", 0, 5);
        List<EmsDatabaseEvidence> evidence = evidenceArray(value.get("evidence"));
        try {
            ProcessArtifactPlan processPlan = value.has("processPlan")
                    ? ProcessArtifactPlan.fromJson(value.get("processPlan")) : null;
            return new BusinessHandlingDraft(text(value, "subjectKey", 200), text(value, "objectKey", 200),
                    text(value, "recommendedGoal", 2000), alternatives, text(value, "reason", 2000),
                    textValue(value.get("conclusion"), "conclusion", 2000), evidence, processPlan);
        } catch (IllegalArgumentException exception) {
            throw new BusinessModelException("模型返回的业务处理初稿格式不正确", exception);
        }
    }

    /** 解析模型返回的关联证据数组，仅接受受限的行列结构。 */
    private static List<EmsDatabaseEvidence> evidenceArray(JsonNode value) {
        if (value == null || !value.isArray() || value.size() > 10) {
            throw new BusinessModelException("模型返回字段格式不正确: evidence");
        }
        List<EmsDatabaseEvidence> result = new ArrayList<>();
        value.forEach(item -> {
            if (!item.isObject()) {
                throw new BusinessModelException("模型返回字段格式不正确: evidence");
            }
            try {
                String schema = textValue(item.get("schema"), "evidence.schema", 100);
                String table = textValue(item.get("table"), "evidence.table", 100);
                String purpose = textValue(item.get("purpose"), "evidence.purpose", 300);
                List<String> columns = textArray(item.get("columns"), "evidence.columns", 1, 20);
                List<List<String>> rows = new ArrayList<>();
                if (item.has("rows") && item.get("rows").isArray()) {
                    item.get("rows").forEach(row -> {
                        List<String> cells = new ArrayList<>();
                        if (row.isArray()) {
                            row.forEach(cell -> cells.add(cell.isNull() ? "" : cell.asText()));
                        }
                        rows.add(List.copyOf(cells));
                    });
                }
                result.add(new EmsDatabaseEvidence(schema, table, purpose, columns, rows));
            } catch (IllegalArgumentException exception) {
                throw new BusinessModelException("模型返回字段格式不正确: evidence", exception);
            }
        });
        return List.copyOf(result);
    }

    /**
     *
     * @description: 将受限草稿转换为模型输入摘要，不携带前端未定义字段。
     *
     * @return: 严格字段集合的 JSON 对象
     * @author: yigeyige99
     */
    public ObjectNode toJson() {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("subjectKey", subjectKey);
        node.put("objectKey", objectKey);
        node.put("recommendedGoal", recommendedGoal);
        node.putPOJO("alternativeGoals", alternativeGoals);
        node.put("reason", reason);
        node.put("conclusion", conclusion);
        node.putPOJO("evidence", evidence);
        if (processPlan != null) {
            node.set("processPlan", processPlan.toJson());
        }
        return node;
    }

    private static void requireExactObject(JsonNode value) {
        if (value == null || !value.isObject() || value.size() != FIELDS.size() + (value.has("processPlan") ? 1 : 0)) {
            throw new BusinessModelException("模型返回内容不符合约定结构");
        }
        Iterator<String> names = value.fieldNames();
        while (names.hasNext()) {
            String field = names.next();
            if (!FIELDS.contains(field) && !"processPlan".equals(field)) {
                throw new BusinessModelException("模型返回内容包含未约定字段");
            }
        }
    }

    private static List<String> textArray(JsonNode value, String field, int minSize, int maxSize) {
        if (value == null || !value.isArray() || value.size() < minSize || value.size() > maxSize) {
            throw new BusinessModelException("模型返回字段格式不正确: " + field);
        }
        List<String> values = new ArrayList<>();
        value.forEach(item -> values.add(textValue(item, field, 2000)));
        return values;
    }

    private static String text(JsonNode value, String field, int maxLength) {
        return textValue(value.get(field), field, maxLength);
    }

    private static String textValue(JsonNode value, String field, int maxLength) {
        if (value == null || !value.isTextual() || value.textValue().isBlank() || value.textValue().length() > maxLength) {
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
