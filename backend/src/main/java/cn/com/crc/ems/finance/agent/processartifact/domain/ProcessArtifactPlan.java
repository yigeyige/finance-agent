package cn.com.crc.ems.finance.agent.processartifact.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @Description: 流程文件场景的受限中间表示，保存节点、流转、可选配置与待补信息，不包含 EMS 分配的标识。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 12:20:00
 */
public record ProcessArtifactPlan(String name, List<Node> nodes, List<Flow> flows,
        List<ObjectNode> nodeParameters, List<ObjectNode> departmentApprovals, List<ObjectNode> approvalRules,
        List<String> missingItems, List<String> unsupported) {

    private static final Set<String> FIELDS = Set.of("name", "nodes", "flows", "nodeParameters",
            "departmentApprovals", "approvalRules", "missingItems", "unsupported");
    private static final Set<String> KINDS = Set.of("draft", "approve", "accountant", "task-pool",
            "import-erp", "exclusive-gateway");
    private static final Pattern ID = Pattern.compile("[A-Za-z][A-Za-z0-9_]{0,63}");
    private static final Pattern VALUE = Pattern.compile("[A-Za-z0-9_-]{1,100}");

    public ProcessArtifactPlan {
        requireText(name, 100, "流程名称");
        if (name.contains("..") || name.matches(".*[\\\\/:*?\"<>|\\p{Cntrl}].*")) {
            throw new IllegalArgumentException("流程名称不能包含路径字符");
        }
        nodes = List.copyOf(nodes);
        flows = List.copyOf(flows);
        if (nodes.size() > 100 || flows.size() > 200) {
            throw new IllegalArgumentException("流程节点或流转超过首版限制");
        }
        nodeParameters = copyObjects(nodeParameters, 100);
        departmentApprovals = copyObjects(departmentApprovals, 50);
        approvalRules = copyObjects(approvalRules, 50);
        missingItems = copyTexts(missingItems, 100);
        unsupported = copyTexts(unsupported, 100);
        Set<String> ids = new HashSet<>(Set.of("start", "end"));
        nodes.forEach(node -> {
            if (!ids.add(node.id())) throw new IllegalArgumentException("流程节点 ID 重复");
        });
        flows.forEach(flow -> {
            if (!ids.contains(flow.source()) || !ids.contains(flow.target()) || flow.source().equals(flow.target())) {
                throw new IllegalArgumentException("流程流转引用了不存在的节点或自身");
            }
        });
        nodeParameters.forEach(item -> {
            if (!ids.contains(item.path("actId").asText())) {
                throw new IllegalArgumentException("节点参数引用了不存在的流程节点");
            }
        });
        departmentApprovals.forEach(item -> {
            if (!ids.contains(item.path("approvalNode").asText())) {
                throw new IllegalArgumentException("部门审批流引用了不存在的流程节点");
            }
        });
    }

    /** 严格读取模型返回的流程结构并检查节点引用与受支持类型。 */
    public static ProcessArtifactPlan fromJson(JsonNode value) {
        requireFields(value, FIELDS, "流程结构");
        List<Node> nodes = new ArrayList<>();
        array(value.get("nodes"), "节点").forEach(item -> {
            requireFields(item, Set.of("id", "kind", "name", "assignee"), "节点");
            nodes.add(new Node(text(item, "id", 64), text(item, "kind", 40),
                    text(item, "name", 100), optionalText(item, "assignee", 200)));
        });
        List<Flow> flows = new ArrayList<>();
        array(value.get("flows"), "流转").forEach(item -> {
            requireFields(item, Set.of("source", "target", "condition"), "流转");
            JsonNode raw = item.get("condition");
            Condition condition = raw == null || raw.isNull() ? null : Condition.fromJson(raw);
            flows.add(new Flow(text(item, "source", 64), text(item, "target", 64), condition));
        });
        return new ProcessArtifactPlan(text(value, "name", 100), nodes, flows,
                objects(value.get("nodeParameters"), "节点参数"),
                objects(value.get("departmentApprovals"), "部门审批流"),
                objects(value.get("approvalRules"), "审批规则"),
                texts(value.get("missingItems"), "待补信息"), texts(value.get("unsupported"), "未支持形态"));
    }

    /** 判断节点 ID 是否符合首版编辑器约束。 */
    static boolean isValidNodeId(String id) {
        return id != null && ID.matcher(id).matches();
    }

    /** 判断节点类型是否在首版支持范围内。 */
    static boolean isSupportedKind(String kind) {
        return kind != null && KINDS.contains(kind);
    }

    /** 判断排他分支三元组是否满足白名单约束。 */
    static boolean isValidCondition(String variable, String operator, String value) {
        return variable != null && ID.matcher(variable).matches()
                && Set.of("==", "!=", ">", ">=", "<", "<=").contains(operator)
                && value != null && VALUE.matcher(value).matches();
    }

    /** 把已校验的中间表示交给模型和批次存储，字段集合保持固定。 */
    public ObjectNode toJson() {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("name", name);
        node.putPOJO("nodes", nodes);
        node.putPOJO("flows", flows);
        node.set("nodeParameters", JsonNodeFactory.instance.arrayNode().addAll(nodeParameters));
        node.set("departmentApprovals", JsonNodeFactory.instance.arrayNode().addAll(departmentApprovals));
        node.set("approvalRules", JsonNodeFactory.instance.arrayNode().addAll(approvalRules));
        node.putPOJO("missingItems", missingItems);
        node.putPOJO("unsupported", unsupported);
        return node;
    }

    /** 编辑器节点标识在单个流程内唯一；五种用户任务和排他网关为首版支持范围。 */
    public record Node(String id, String kind, String name, String assignee) {
        public Node {
            if (id == null || !ID.matcher(id).matches()) throw new IllegalArgumentException("流程节点 ID 无效");
            if (!KINDS.contains(kind)) throw new IllegalArgumentException("不支持的节点类型");
            requireText(name, 100, "节点名称");
            assignee = assignee == null ? "" : assignee;
            if (assignee.length() > 200) throw new IllegalArgumentException("处理人超出限制");
        }
    }

    /** 流转只接受内部节点引用；条件由受限三元组生成，不接收任意表达式。 */
    public record Flow(String source, String target, Condition condition) { }

    /** 排他分支条件的变量、运算符和值均经过白名单检查。 */
    public record Condition(String variable, String operator, String value) {
        public Condition {
            if (variable == null || !ID.matcher(variable).matches()
                    || !Set.of("==", "!=", ">", ">=", "<", "<=").contains(operator)
                    || value == null || !VALUE.matcher(value).matches()) {
                throw new IllegalArgumentException("分支条件格式不正确");
            }
        }

        private static Condition fromJson(JsonNode value) {
            requireFields(value, Set.of("variable", "operator", "value"), "分支条件");
            return new Condition(text(value, "variable", 64), text(value, "operator", 2),
                    text(value, "value", 100));
        }
    }

    private static void requireFields(JsonNode value, Set<String> fields, String label) {
        if (value == null || !value.isObject() || value.size() != fields.size()) {
            throw new IllegalArgumentException(label + "字段不符合约定");
        }
        value.fieldNames().forEachRemaining(field -> {
            if (!fields.contains(field)) throw new IllegalArgumentException(label + "包含未知字段");
        });
    }

    private static String text(JsonNode value, String field, int max) {
        JsonNode item = value.get(field);
        if (item == null || !item.isTextual()) throw new IllegalArgumentException(field + "格式不正确");
        String result = item.textValue();
        requireText(result, max, field);
        return result;
    }

    private static String optionalText(JsonNode value, String field, int max) {
        JsonNode item = value.get(field);
        if (item == null || !item.isTextual() || item.textValue().length() > max) {
            throw new IllegalArgumentException(field + "格式不正确");
        }
        return item.textValue();
    }

    private static void requireText(String value, int max, String label) {
        if (value == null || value.isBlank() || value.length() > max) {
            throw new IllegalArgumentException(label + "不能为空或超出限制");
        }
    }

    private static ArrayNode array(JsonNode value, String label) {
        if (!(value instanceof ArrayNode array)) throw new IllegalArgumentException(label + "必须为数组");
        return array;
    }

    private static List<ObjectNode> objects(JsonNode value, String label) {
        List<ObjectNode> result = new ArrayList<>();
        array(value, label).forEach(item -> {
            if (!(item instanceof ObjectNode object)) throw new IllegalArgumentException(label + "元素必须为对象");
            result.add(object.deepCopy());
        });
        return result;
    }

    private static List<String> texts(JsonNode value, String label) {
        List<String> result = new ArrayList<>();
        array(value, label).forEach(item -> {
            if (!item.isTextual()) throw new IllegalArgumentException(label + "元素必须为文本");
            requireText(item.textValue(), 300, label);
            result.add(item.textValue());
        });
        return result;
    }

    private static List<ObjectNode> copyObjects(List<ObjectNode> values, int max) {
        if (values == null || values.size() > max) throw new IllegalArgumentException("配置数量超出限制");
        return values.stream().map(ObjectNode::deepCopy).toList();
    }

    private static List<String> copyTexts(List<String> values, int max) {
        if (values == null || values.size() > max) throw new IllegalArgumentException("说明数量超出限制");
        return List.copyOf(values);
    }
}
