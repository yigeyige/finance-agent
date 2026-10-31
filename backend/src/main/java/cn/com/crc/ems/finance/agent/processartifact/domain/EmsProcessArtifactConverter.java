package cn.com.crc.ems.finance.agent.processartifact.domain;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;

/**
 *
 * @Description: 把受限流程中间表示转换为 EMS 编辑器 JSON 与可选配置数组，不生成导入时由 EMS 分配的标识。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 12:25:00
 */
public final class EmsProcessArtifactConverter {

    private static final Set<String> NODE_PARAMETER_FIELDS = Set.of("variableName", "actId", "variableValue",
            "variableNote", "variableIsnull", "usednextact", "actName", "variableDefaultvalue",
            "variableFormat", "scope", "variableDatatype");
    private static final Set<String> DEPARTMENT_FIELDS = Set.of("business", "businessName", "orgCode", "orgName",
            "role", "roleName", "approvalNode", "approvalNodeName", "nodeRule", "businessNodeAs", "valueUrl",
            "startAmount", "companyCode", "companyName", "endAmount", "nodeRuleName", "valueUrlName", "remarks");
    private static final Set<String> RULE_HEAD_FIELDS = Set.of("categoryCode", "categoryName", "businessLineCode",
            "businessLineName", "orgTypeCode", "orgTypeName", "orgCode", "orgName", "workflowLineKey",
            "workflowLineKey1", "ouCode", "ouName", "areaValue");
    private static final Set<String> RULE_LINE_FIELDS = Set.of("workflowLineValue", "applyAmountMin",
            "applyAmountMax", "payAmountMin", "payAmountMax", "currency");

    /** 生成根目录可导入的模型文件及适用配置；未连通流程标为待修复。 */
    public EmsProcessArtifactFiles convert(ProcessArtifactPlan plan) {
        Map<String, String> resources = new HashMap<>();
        resources.put("start", resource());
        resources.put("end", resource());
        plan.nodes().forEach(node -> resources.put(node.id(), resource()));
        ObjectNode model = model(plan.name());
        ArrayNode shapes = model.withArray("childShapes");
        Map<String, ObjectNode> shapeById = new HashMap<>();
        shapeById.put("start", shape("start", "StartNoneEvent", "开始", resources.get("start"), 0));
        int position = 1;
        for (ProcessArtifactPlan.Node node : plan.nodes()) {
            shapeById.put(node.id(), shape(node.id(), "exclusive-gateway".equals(node.kind())
                    ? "ExclusiveGateway" : "UserTask", node.name(), resources.get(node.id()), position++));
            ObjectNode props = (ObjectNode) shapeById.get(node.id()).get("properties");
            if (!"exclusive-gateway".equals(node.kind())) {
                props.put("node_type", nodeType(node.kind()));
                props.put("usertaskassignment", node.assignee());
                props.put("multiinstance_type", "None");
                props.put("retract_start", "false");
                props.put("join_skip", "false");
                props.put("asynchronousdefinition", "false");
                props.put("exclusivedefinition", "false");
                props.put("isforcompensation", "false");
                props.put("formkeydefinition", "");
                props.put("duedatedefinition", "");
                props.put("prioritydefinition", "");
                props.put("formproperties", "");
                props.put("tasklisteners", "");
            }
        }
        shapeById.put("end", shape("end", "EndNoneEvent", "结束", resources.get("end"), position));
        shapes.add(shapeById.get("start"));
        plan.nodes().forEach(node -> shapes.add(shapeById.get(node.id())));
        shapes.add(shapeById.get("end"));
        for (ProcessArtifactPlan.Flow flow : plan.flows()) {
            String flowId = resource();
            ((ArrayNode) shapeById.get(flow.source()).get("outgoing")).addObject().put("resourceId", flowId);
            ObjectNode line = shape("", "SequenceFlow", "", flowId, position++);
            ObjectNode properties = (ObjectNode) line.get("properties");
            properties.put("conditionsequenceflow", flow.condition() == null ? "" : expression(flow.condition()));
            properties.put("executionlisteners", "");
            properties.put("defaultflow", "false");
            properties.put("showdiamondmarker", false);
            line.putObject("target").put("resourceId", resources.get(flow.target()));
            line.withArray("outgoing").addObject().put("resourceId", resources.get(flow.target()));
            ArrayNode dockers = line.putArray("dockers");
            dockers.addObject().put("x", 50).put("y", 40);
            dockers.addObject().put("x", 50).put("y", 40);
            shapes.add(line);
        }
        List<String> missing = new ArrayList<>(plan.missingItems());
        missing.addAll(plan.unsupported().stream().map(value -> "暂未转换：" + value).toList());
        boolean connected = connected(plan);
        if (!connected) missing.add("流程没有从开始到结束的完整路径，或存在未连通节点");
        if (plan.nodes().isEmpty()) missing.add("流程尚无可用任务节点");
        plan.approvalRules().forEach(rule -> {
            if (!rule.path("head").isObject() || !rule.path("lineList").isArray()
                    || rule.path("lineList").isEmpty()) {
                missing.add("审批规则缺少表头或档位明细");
            } else {
                rule.path("lineList").forEach(line -> {
                    if (!line.isObject() || !rangeValid(line, "applyAmountMin", "applyAmountMax")
                            || !rangeValid(line, "payAmountMin", "payAmountMax")) {
                        missing.add("审批规则档位范围不正确");
                    }
                });
            }
        });
        for (ProcessArtifactPlan.Node node : plan.nodes()) {
            if ("exclusive-gateway".equals(node.kind())) {
                List<ProcessArtifactPlan.Flow> outgoing = plan.flows().stream()
                        .filter(flow -> flow.source().equals(node.id())).toList();
                if (outgoing.size() < 2 || outgoing.stream().filter(flow -> flow.condition() == null).count() > 1) {
                    missing.add("排他分支 " + node.name() + " 缺少有效出口条件");
                }
            }
        }
        String status = connected && !plan.nodes().isEmpty() && missing.stream()
                .noneMatch(value -> value.startsWith("排他分支") || value.startsWith("审批规则"))
                ? "IMPORT_CANDIDATE" : "NEEDS_REPAIR";
        return new EmsProcessArtifactFiles(plan.name() + "BPMN.xml", CanonicalJsonCodec.canonicalize(model),
                nodeParameters(plan), departmentApprovals(plan), approvalRules(plan), List.copyOf(missing), status);
    }

    private static ObjectNode model(String name) {
        ObjectNode root = JsonNodeFactory.instance.objectNode();
        root.put("resourceId", "canvas");
        ObjectNode props = root.putObject("properties");
        props.put("process_id", "");
        props.put("name", name);
        props.put("documentation", "");
        props.put("process_author", "");
        props.put("process_version", "");
        props.put("process_namespace", "http://www.activiti.org/processdef");
        props.put("executionlisteners", "");
        props.put("eventlisteners", "");
        props.put("signaldefinitions", "");
        props.put("messagedefinitions", "");
        props.put("conditionsequenceflow", "");
        root.putObject("stencil").put("id", "BPMNDiagram");
        root.putArray("childShapes");
        bounds(root, 0, 0, 3000, 3000);
        ObjectNode stencilset = root.putObject("stencilset");
        stencilset.put("url", "stencilsets/bpmn2.0/bpmn2.0.json");
        stencilset.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        root.putArray("ssextensions");
        return root;
    }

    private static ObjectNode shape(String id, String stencil, String name, String resourceId, int position) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("resourceId", resourceId);
        ObjectNode props = node.putObject("properties");
        props.put("overrideid", id);
        props.put("name", name);
        props.put("documentation", "");
        props.put("executionlisteners", "");
        node.putObject("stencil").put("id", stencil);
        node.putArray("childShapes");
        node.putArray("outgoing");
        bounds(node, 40 + position * 160, 140, 140 + position * 160, 220);
        node.putArray("dockers");
        return node;
    }

    private static void bounds(ObjectNode node, int left, int top, int right, int bottom) {
        ObjectNode bounds = node.putObject("bounds");
        bounds.putObject("lowerRight").put("x", right).put("y", bottom);
        bounds.putObject("upperLeft").put("x", left).put("y", top);
    }

    private static String nodeType(String kind) {
        return switch (kind) {
            case "draft" -> "0";
            case "approve" -> "1";
            case "accountant" -> "2";
            case "task-pool" -> "4";
            case "import-erp" -> "5";
            default -> throw new IllegalArgumentException("不支持的节点类型");
        };
    }

    private static String expression(ProcessArtifactPlan.Condition condition) {
        return "${" + condition.variable() + " " + condition.operator() + "'" + condition.value() + "'}";
    }

    private static String resource() {
        return "sid-" + UUID.randomUUID().toString().toUpperCase();
    }

    private static boolean connected(ProcessArtifactPlan plan) {
        if (plan.flows().stream().anyMatch(flow -> "end".equals(flow.source())
                || "start".equals(flow.target()))) return false;
        Set<String> forward = reachable(plan, "start", false);
        Set<String> backward = reachable(plan, "end", true);
        if (!forward.contains("end")) return false;
        return plan.nodes().stream().allMatch(node -> forward.contains(node.id()) && backward.contains(node.id()));
    }

    private static Set<String> reachable(ProcessArtifactPlan plan, String origin, boolean reverse) {
        Set<String> visited = new HashSet<>();
        ArrayDeque<String> queue = new ArrayDeque<>();
        queue.add(origin);
        while (!queue.isEmpty()) {
            String current = queue.removeFirst();
            if (!visited.add(current)) continue;
            plan.flows().stream().filter(flow -> (reverse ? flow.target() : flow.source()).equals(current))
                    .forEach(flow -> queue.add(reverse ? flow.source() : flow.target()));
        }
        return visited;
    }

    private static String nodeParameters(ProcessArtifactPlan plan) {
        if (plan.nodeParameters().isEmpty()) return null;
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        plan.nodeParameters().forEach(source -> {
            ObjectNode item = allowed(source, NODE_PARAMETER_FIELDS);
            item.putNull("id");
            item.put("procdefId", "");
            array.add(item);
        });
        return CanonicalJsonCodec.canonicalize(array);
    }

    private static String departmentApprovals(ProcessArtifactPlan plan) {
        if (plan.departmentApprovals().isEmpty()) return null;
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        plan.departmentApprovals().forEach(source -> {
            ObjectNode item = allowed(source, DEPARTMENT_FIELDS);
            item.putNull("id");
            item.put("processTemplate", plan.name());
            item.put("processTemplateCode", "");
            array.add(item);
        });
        return CanonicalJsonCodec.canonicalize(array);
    }

    private static String approvalRules(ProcessArtifactPlan plan) {
        if (plan.approvalRules().isEmpty()) return null;
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        plan.approvalRules().forEach(source -> {
            JsonNode rawHead = source.get("head");
            JsonNode rawLines = source.get("lineList");
            ObjectNode head = rawHead instanceof ObjectNode object ? object : JsonNodeFactory.instance.objectNode();
            ArrayNode lines = rawLines instanceof ArrayNode rawArray ? rawArray : JsonNodeFactory.instance.arrayNode();
            ObjectNode item = JsonNodeFactory.instance.objectNode();
            ObjectNode safeHead = allowed(head, RULE_HEAD_FIELDS);
            safeHead.putNull("rulesHeadId");
            safeHead.put("processId", "");
            safeHead.put("processName", plan.name());
            item.set("head", safeHead);
            ArrayNode safeLines = item.putArray("lineList");
            lines.forEach(line -> {
                ObjectNode object = line instanceof ObjectNode value ? value : JsonNodeFactory.instance.objectNode();
                ObjectNode safe = allowed(object, RULE_LINE_FIELDS);
                safe.putNull("rulesLineId");
                safe.putNull("rulesHeadId");
                safeLines.add(safe);
            });
            array.add(item);
        });
        return CanonicalJsonCodec.canonicalize(array);
    }

    private static ObjectNode allowed(ObjectNode source, Set<String> fields) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        fields.forEach(field -> {
            if (source.has(field)) output.set(field, source.get(field));
        });
        return output;
    }

    private static boolean rangeValid(JsonNode line, String minField, String maxField) {
        try {
            BigDecimal min = amount(line.get(minField));
            BigDecimal max = amount(line.get(maxField));
            return min == null || max == null || min.compareTo(max) <= 0;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private static BigDecimal amount(JsonNode value) {
        if (value == null || value.isNull() || value.asText().isBlank()) return null;
        return new BigDecimal(value.asText());
    }
}
