package cn.com.crc.ems.finance.agent.processartifact.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @Description: 对模型返回的流程结构做安全规范化和降级，不改变业务值，仅修复结构偏差。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 17:30:00
 */
public final class ProcessArtifactPlanNormalizer {

    private ProcessArtifactPlanNormalizer() {
    }

    /** 规范化结果；plan 为空表示无法安全修复。 */
    public record Result(ProcessArtifactPlan plan, List<String> warnings) {
        public Result {
            warnings = warnings == null ? List.of() : List.copyOf(warnings);
        }
    }

    /** 规范化模型返回的流程结构并收集可展示的修正说明。 */
    public static Result normalize(JsonNode value) {
        List<String> warnings = new ArrayList<>();
        if (value == null || !value.isObject()) {
            warnings.add("未返回可解析的流程结构");
            return new Result(null, warnings);
        }
        String name = normalizeName(value.get("name"), warnings);
        NodeNormalization nodeResult = normalizeNodes(value.get("nodes"), warnings);
        List<ProcessArtifactPlan.Flow> flows = normalizeFlows(value.get("flows"), nodeResult.idMapping(), warnings);
        List<ObjectNode> nodeParameters = normalizeReferences(value.get("nodeParameters"), "actId",
                nodeResult.idMapping(), "节点参数", warnings);
        List<ObjectNode> departmentApprovals = normalizeReferences(value.get("departmentApprovals"), "approvalNode",
                nodeResult.idMapping(), "部门审批流", warnings);
        List<ObjectNode> approvalRules = normalizeObjects(value.get("approvalRules"), "审批规则", warnings);
        List<String> missingItems = normalizeTexts(value.get("missingItems"), "待补信息", warnings);
        List<String> unsupported = normalizeTexts(value.get("unsupported"), "未支持形态", warnings);
        try {
            return new Result(new ProcessArtifactPlan(name, nodeResult.nodes(), flows, nodeParameters,
                    departmentApprovals, approvalRules, missingItems, unsupported), warnings);
        } catch (IllegalArgumentException exception) {
            warnings.add("流程结构无法规范化：" + safeMessage(exception));
            return new Result(null, warnings);
        }
    }

    private static String normalizeName(JsonNode value, List<String> warnings) {
        String raw = textOrEmpty(value);
        if (raw.isBlank()) {
            warnings.add("流程名称缺失，已使用未命名流程");
            return "未命名流程";
        }
        String cleaned = sanitizeName(raw);
        if (!cleaned.equals(raw)) {
            warnings.add("流程名称包含路径字符，已替换");
        }
        if (cleaned.length() > 100) {
            cleaned = cleaned.substring(0, 100);
            warnings.add("流程名称超长，已截断");
        }
        return cleaned;
    }

    private static NodeNormalization normalizeNodes(JsonNode value, List<String> warnings) {
        if (value == null || value.isNull()) {
            warnings.add("未返回流程节点");
            return new NodeNormalization(List.of(), Map.of());
        }
        if (!value.isArray()) {
            warnings.add("流程节点不是数组，已忽略");
            return new NodeNormalization(List.of(), Map.of());
        }
        List<ProcessArtifactPlan.Node> nodes = new ArrayList<>();
        Map<String, String> idMapping = new LinkedHashMap<>();
        Set<String> usedIds = new HashSet<>(Set.of("start", "end"));
        int index = 0;
        for (JsonNode item : value) {
            index++;
            if (!item.isObject()) {
                warnings.add("第 " + index + " 个节点不是对象，已忽略");
                continue;
            }
            String rawId = textOrEmpty(item.get("id"));
            String rawKind = textOrEmpty(item.get("kind"));
            if (isReservedStartOrEnd(rawId, rawKind)) {
                warnings.add("已移除显式起止节点：" + label(rawId, index));
                continue;
            }
            String kind = normalizeKind(rawKind);
            if (kind == null) {
                warnings.add("节点 " + label(rawId, index) + " 的类型 " + rawKind + " 不受支持，已忽略");
                continue;
            }
            String id = normalizeId(rawId, index, usedIds, warnings);
            if (!rawId.isBlank()) {
                idMapping.put(rawId, id);
            }
            String name = textOrEmpty(item.get("name"));
            if (name.isBlank()) {
                name = id;
                warnings.add("节点 " + id + " 缺少名称，已使用标识代替");
            }
            name = truncate(name, 100);
            String assignee = "";
            if (item.hasNonNull("assignee") && item.get("assignee").isTextual()) {
                assignee = truncate(item.get("assignee").textValue(), 200);
            } else {
                warnings.add("节点 " + id + " 的处理人已补为空");
            }
            try {
                nodes.add(new ProcessArtifactPlan.Node(id, kind, name, assignee));
            } catch (IllegalArgumentException exception) {
                warnings.add("节点 " + label(rawId, index) + " 无法修复，已忽略：" + safeMessage(exception));
            }
        }
        if (nodes.isEmpty()) {
            warnings.add("未识别到可用流程节点");
        }
        return new NodeNormalization(nodes, idMapping);
    }

    private static List<ProcessArtifactPlan.Flow> normalizeFlows(JsonNode value, Map<String, String> idMapping,
            List<String> warnings) {
        if (value == null || value.isNull()) {
            warnings.add("未返回流程流转");
            return List.of();
        }
        if (!value.isArray()) {
            warnings.add("流程流转不是数组，已忽略");
            return List.of();
        }
        List<ProcessArtifactPlan.Flow> flows = new ArrayList<>();
        int index = 0;
        for (JsonNode item : value) {
            index++;
            if (!item.isObject()) {
                warnings.add("第 " + index + " 条流转不是对象，已忽略");
                continue;
            }
            String source = resolveNodeId(textOrEmpty(item.get("source")), idMapping);
            String target = resolveNodeId(textOrEmpty(item.get("target")), idMapping);
            if (source == null) {
                warnings.add("第 " + index + " 条流转引用了不存在的源节点，已忽略");
                continue;
            }
            if (target == null) {
                warnings.add("第 " + index + " 条流转引用了不存在的目标节点，已忽略");
                continue;
            }
            if (source.equals(target)) {
                warnings.add("第 " + index + " 条流转为自环，已忽略");
                continue;
            }
            ProcessArtifactPlan.Condition condition = normalizeCondition(item.get("condition"), index, warnings);
            flows.add(new ProcessArtifactPlan.Flow(source, target, condition));
        }
        return flows;
    }

    private static ProcessArtifactPlan.Condition normalizeCondition(JsonNode value, int index, List<String> warnings) {
        if (value == null || value.isNull()) {
            return null;
        }
        if (!value.isObject()) {
            warnings.add("第 " + index + " 条流转的条件格式不正确，已改为无条件流转");
            return null;
        }
        String variable = textOrEmpty(value.get("variable"));
        String operator = textOrEmpty(value.get("operator"));
        String conditionValue = textOrEmpty(value.get("value"));
        if (!ProcessArtifactPlan.isValidCondition(variable, operator, conditionValue)) {
            warnings.add("第 " + index + " 条流转的条件不合法，已改为无条件流转");
            return null;
        }
        return new ProcessArtifactPlan.Condition(variable, operator, conditionValue);
    }

    private static List<ObjectNode> normalizeReferences(JsonNode value, String field, Map<String, String> idMapping,
            String label, List<String> warnings) {
        List<ObjectNode> result = new ArrayList<>();
        if (value == null || value.isNull()) {
            return result;
        }
        if (!value.isArray()) {
            warnings.add(label + "不是数组，已忽略");
            return result;
        }
        int index = 0;
        for (JsonNode item : value) {
            index++;
            if (!item.isObject()) {
                warnings.add(label + "第 " + index + " 项不是对象，已忽略");
                continue;
            }
            String reference = resolveNodeId(textOrEmpty(item.get(field)), idMapping);
            if (reference == null) {
                warnings.add(label + "第 " + index + " 项引用了不存在的节点，已忽略");
                continue;
            }
            ObjectNode copy = ((ObjectNode) item).deepCopy();
            copy.put(field, reference);
            result.add(copy);
        }
        return result;
    }

    private static List<ObjectNode> normalizeObjects(JsonNode value, String label, List<String> warnings) {
        List<ObjectNode> result = new ArrayList<>();
        if (value == null || value.isNull()) {
            return result;
        }
        if (!value.isArray()) {
            warnings.add(label + "不是数组，已忽略");
            return result;
        }
        int index = 0;
        for (JsonNode item : value) {
            index++;
            if (!item.isObject()) {
                warnings.add(label + "第 " + index + " 项不是对象，已忽略");
                continue;
            }
            result.add(((ObjectNode) item).deepCopy());
        }
        return result;
    }

    private static List<String> normalizeTexts(JsonNode value, String label, List<String> warnings) {
        List<String> result = new ArrayList<>();
        if (value == null || value.isNull()) {
            return result;
        }
        if (!value.isArray()) {
            warnings.add(label + "不是数组，已忽略");
            return result;
        }
        int index = 0;
        for (JsonNode item : value) {
            index++;
            if (!item.isTextual() || item.textValue().isBlank()) {
                warnings.add(label + "第 " + index + " 项为空或不是文本，已忽略");
                continue;
            }
            String text = item.textValue();
            String truncated = truncate(text, 300);
            if (truncated.length() != text.length()) {
                warnings.add(label + "第 " + index + " 项超长，已截断");
            }
            result.add(truncated);
        }
        return result;
    }

    private static String normalizeId(String raw, int index, Set<String> usedIds, List<String> warnings) {
        String base = raw == null ? "" : raw.trim();
        if (base.isBlank()) {
            base = "node_" + index;
            warnings.add("节点缺少 ID，已生成 " + base);
        }
        StringBuilder builder = new StringBuilder();
        for (char c : base.toCharArray()) {
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_') {
                builder.append(c);
            } else {
                builder.append('_');
            }
        }
        String cleaned = builder.toString();
        if (cleaned.isEmpty() || !((cleaned.charAt(0) >= 'A' && cleaned.charAt(0) <= 'Z')
                || (cleaned.charAt(0) >= 'a' && cleaned.charAt(0) <= 'z'))) {
            cleaned = "n_" + cleaned;
        }
        cleaned = truncate(cleaned, 64);
        String unique = cleaned;
        int suffix = 2;
        while (usedIds.contains(unique)) {
            unique = truncate(cleaned, 60) + "_" + suffix;
            suffix++;
        }
        if (!unique.equals(base)) {
            warnings.add("节点 ID " + base + " 已规范化为 " + unique);
        }
        usedIds.add(unique);
        return unique;
    }

    private static String resolveNodeId(String raw, Map<String, String> idMapping) {
        if (isImplicitNode(raw)) {
            return raw;
        }
        return idMapping.get(raw);
    }

    private static String normalizeKind(String raw) {
        String kind = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        return switch (kind) {
            case "gateway", "exclusive_gateway", "exclusivegateway" -> "exclusive-gateway";
            case "approval", "approve_task", "approvetask" -> "approve";
            case "accountant_task", "accountanttask" -> "accountant";
            case "taskpool", "task_pool" -> "task-pool";
            case "import_erp", "importerp" -> "import-erp";
            default -> ProcessArtifactPlan.isSupportedKind(kind) ? kind : null;
        };
    }

    private static boolean isReservedStartOrEnd(String id, String kind) {
        return "start".equalsIgnoreCase(id) || "end".equalsIgnoreCase(id)
                || "start".equalsIgnoreCase(kind) || "end".equalsIgnoreCase(kind);
    }

    private static boolean isImplicitNode(String id) {
        return "start".equals(id) || "end".equals(id);
    }

    private static String sanitizeName(String value) {
        StringBuilder builder = new StringBuilder();
        for (char c : value.toCharArray()) {
            if (c == (char) 92 || c == '/' || c == ':' || c == '*' || c == '?' || c == (char) 34
                    || c == '<' || c == '>' || c == '|' || Character.isISOControl(c)) {
                builder.append('_');
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private static String textOrEmpty(JsonNode value) {
        return value != null && value.isTextual() ? value.textValue() : "";
    }

    private static String label(String id, int index) {
        return id == null || id.isBlank() ? "第 " + index + " 个节点" : id;
    }

    private static String truncate(String value, int max) {
        return value.length() <= max ? value : value.substring(0, max);
    }

    private static String safeMessage(Throwable exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? exception.getClass().getSimpleName() : message;
    }

    private record NodeNormalization(List<ProcessArtifactPlan.Node> nodes, Map<String, String> idMapping) {
    }
}
