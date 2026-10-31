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

    private static final int X_BASE = 40;
    private static final int LAYER_GAP = 220;
    private static final int ROW_GAP = 120;
    private static final int LANE_GAP = 140;
    private static final int CENTER_Y = 300;
    private static final int NODE_WIDTH = 120;
    private static final int NODE_HEIGHT = 60;
    private static final int GATEWAY_WIDTH = 40;
    private static final int GATEWAY_HEIGHT = 40;
    private static final int EVENT_SIZE = 40;

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
        Layout layout = layout(plan);
        Map<String, String> resources = new HashMap<>();
        resources.put("start", resource());
        resources.put("end", resource());
        plan.nodes().forEach(node -> resources.put(node.id(), resource()));
        ObjectNode model = model(plan.name());
        ArrayNode shapes = model.withArray("childShapes");
        Map<String, ObjectNode> shapeById = new HashMap<>();
        shapeById.put("start", shape("start", "StartNoneEvent", "开始", resources.get("start"), layout.boxes().get("start")));
        for (ProcessArtifactPlan.Node node : plan.nodes()) {
            Box box = layout.boxes().get(node.id());
            shapeById.put(node.id(), shape(node.id(), "exclusive-gateway".equals(node.kind())
                    ? "ExclusiveGateway" : "UserTask", node.name(), resources.get(node.id()), box));
            ObjectNode props = (ObjectNode) shapeById.get(node.id()).get("properties");
            if ("exclusive-gateway".equals(node.kind())) {
                props.put("sequencefloworder", "");
            } else {
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
        shapeById.put("end", shape("end", "EndNoneEvent", "结束", resources.get("end"), layout.boxes().get("end")));
        shapes.add(shapeById.get("start"));
        plan.nodes().forEach(node -> shapes.add(shapeById.get(node.id())));
        shapes.add(shapeById.get("end"));
        for (ProcessArtifactPlan.Flow flow : plan.flows()) {
            String flowId = resource();
            ((ArrayNode) shapeById.get(flow.source()).get("outgoing")).addObject().put("resourceId", flowId);
            ObjectNode line = lineShape(flowId, resources.get(flow.target()), layout.boxes().get(flow.source()), layout.boxes().get(flow.target()));
            ObjectNode properties = (ObjectNode) line.get("properties");
            properties.put("conditionsequenceflow", flow.condition() == null ? "" : expression(flow.condition()));
            shapes.add(line);
        }
        List<String> missing = new ArrayList<>(plan.missingItems());
        if (!layout.acyclic()) {
            missing.add("流程存在回边或循环，已标记待修复");
        }
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

    private static ObjectNode shape(String id, String stencil, String name, String resourceId, Box box) {
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
        bounds(node, box.x(), box.y(), box.right(), box.bottom());
        node.putArray("dockers");
        return node;
    }

    private static ObjectNode lineShape(String flowId, String targetResourceId, Box source, Box target) {
        ObjectNode line = JsonNodeFactory.instance.objectNode();
        line.put("resourceId", flowId);
        ObjectNode props = line.putObject("properties");
        props.put("name", "");
        props.put("documentation", "");
        props.put("executionlisteners", "");
        props.put("conditionsequenceflow", "");
        props.put("defaultflow", "false");
        props.put("showdiamondmarker", false);
        line.putObject("stencil").put("id", "SequenceFlow");
        line.putArray("childShapes");
        line.putArray("outgoing");
        line.putObject("target").put("resourceId", targetResourceId);
        line.withArray("outgoing").addObject().put("resourceId", targetResourceId);
        Point start = new Point(source.centerX(), source.centerY());
        Point end = new Point(target.centerX(), target.centerY());
        int minX = Math.min(start.x(), end.x());
        int minY = Math.min(start.y(), end.y());
        int maxX = Math.max(start.x(), end.x());
        int maxY = Math.max(start.y(), end.y());
        bounds(line, minX, minY, maxX, maxY);
        ArrayNode dockers = line.putArray("dockers");
        dockers.addObject().put("x", source.width() / 2).put("y", source.height() / 2);
        dockers.addObject().put("x", target.width() / 2).put("y", target.height() / 2);
        return line;
    }

    private static List<Point> route(Box source, Box target) {
        if (target.x() - source.right() > 300) {
            int routeY = Math.max(source.bottom(), target.bottom()) + 80;
            return List.of(new Point(source.centerX(), source.bottom()), new Point(source.centerX(), routeY),
                    new Point(target.centerX(), routeY), new Point(target.centerX(), target.bottom()));
        }
        if (target.x() >= source.right() + 20) {
            int sourceY = source.centerY();
            int targetY = target.centerY();
            if (sourceY == targetY) {
                return List.of(new Point(source.right(), sourceY), new Point(target.x(), targetY));
            }
            int middleX = (source.right() + target.x()) / 2;
            return List.of(new Point(source.right(), sourceY), new Point(middleX, sourceY),
                    new Point(middleX, targetY), new Point(target.x(), targetY));
        }
        if (target.bottom() >= source.bottom()) {
            int routeY = Math.max(source.bottom(), target.bottom()) + 40;
            return List.of(new Point(source.centerX(), source.bottom()), new Point(source.centerX(), routeY),
                    new Point(target.centerX(), routeY), new Point(target.centerX(), target.bottom()));
        }
        int routeY = Math.min(source.y(), target.y()) - 40;
        return List.of(new Point(source.centerX(), source.y()), new Point(source.centerX(), routeY),
                new Point(target.centerX(), routeY), new Point(target.centerX(), target.y()));
    }

    private static Layout layout(ProcessArtifactPlan plan) {
        Map<String, Integer> layers = new HashMap<>();
        layers.put("start", 0);
        plan.nodes().forEach(node -> layers.put(node.id(), 1));
        layers.put("end", 1);
        boolean acyclic = true;
        int maxIterations = plan.nodes().size() + 3;
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            boolean changed = false;
            for (ProcessArtifactPlan.Flow flow : plan.flows()) {
                if ("end".equals(flow.source())) {
                    continue;
                }
                int sourceLayer = layers.getOrDefault(flow.source(), 0);
                int targetLayer = layers.getOrDefault(flow.target(), 1);
                if (sourceLayer + 1 > targetLayer) {
                    layers.put(flow.target(), sourceLayer + 1);
                    changed = true;
                }
            }
            if (!changed) {
                break;
            }
            if (iteration == maxIterations - 1) {
                acyclic = false;
            }
        }
        if (!acyclic) {
            return sequentialLayout(plan);
        }
        int maxNodeLayer = plan.nodes().stream().mapToInt(node -> layers.getOrDefault(node.id(), 1)).max().orElse(0);
        if (layers.getOrDefault("end", 1) <= maxNodeLayer) {
            layers.put("end", maxNodeLayer + 1);
        }
        return buildLayout(plan, layers, computeLanes(plan, layers), true);
    }

    private static Layout sequentialLayout(ProcessArtifactPlan plan) {
        Map<String, Integer> layers = new HashMap<>();
        layers.put("start", 0);
        int position = 1;
        for (ProcessArtifactPlan.Node node : plan.nodes()) {
            layers.put(node.id(), position++);
        }
        layers.put("end", position);
        return buildLayout(plan, layers, computeLanes(plan, layers), false);
    }

    private static Layout buildLayout(ProcessArtifactPlan plan, Map<String, Integer> layers,
            Map<String, Integer> lanes, boolean acyclic) {
        Map<Integer, List<String>> byLayer = new HashMap<>();
        List<String> ordered = new ArrayList<>();
        ordered.add("start");
        plan.nodes().forEach(node -> ordered.add(node.id()));
        ordered.add("end");
        for (String id : ordered) {
            byLayer.computeIfAbsent(layers.getOrDefault(id, 0), key -> new ArrayList<>()).add(id);
        }
        int maxLayer = byLayer.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        Map<String, Box> boxes = new HashMap<>();
        for (int layer = 0; layer <= maxLayer; layer++) {
            List<String> ids = byLayer.getOrDefault(layer, List.of());
            Map<Integer, List<String>> byLane = new HashMap<>();
            for (String id : ids) {
                byLane.computeIfAbsent(lanes.getOrDefault(id, 0), key -> new ArrayList<>()).add(id);
            }
            for (Map.Entry<Integer, List<String>> laneEntry : byLane.entrySet()) {
                List<String> laneIds = laneEntry.getValue();
                int total = Math.max(0, (laneIds.size() - 1) * ROW_GAP);
                for (int index = 0; index < laneIds.size(); index++) {
                    String id = laneIds.get(index);
                    int width = "start".equals(id) || "end".equals(id) ? EVENT_SIZE
                            : isGateway(plan, id) ? GATEWAY_WIDTH : NODE_WIDTH;
                    int height = "start".equals(id) || "end".equals(id) ? EVENT_SIZE
                            : isGateway(plan, id) ? GATEWAY_HEIGHT : NODE_HEIGHT;
                    int x = X_BASE + layer * LAYER_GAP + (NODE_WIDTH - width) / 2;
                    int y = CENTER_Y + laneEntry.getKey() * LANE_GAP - total / 2 + index * ROW_GAP - height / 2;
                    boxes.put(id, new Box(x, y, width, height));
                }
            }
        }
        return new Layout(boxes, acyclic);
    }

    /** 根据排他网关出口分配纵向车道，使分支目标上下错开。 */
    private static Map<String, Integer> computeLanes(ProcessArtifactPlan plan, Map<String, Integer> layers) {
        Map<String, Integer> lanes = new HashMap<>();
        lanes.put("start", 0);
        plan.nodes().forEach(node -> lanes.put(node.id(), 0));
        lanes.put("end", 0);
        for (ProcessArtifactPlan.Node node : plan.nodes()) {
            if (!"exclusive-gateway".equals(node.kind())) {
                continue;
            }
            List<ProcessArtifactPlan.Flow> outgoing = plan.flows().stream()
                    .filter(flow -> flow.source().equals(node.id())).toList();
            int gatewayLane = lanes.getOrDefault(node.id(), 0);
            for (int index = 0; index < outgoing.size(); index++) {
                String target = outgoing.get(index).target();
                if (!"end".equals(target)) {
                    lanes.put(target, gatewayLane + branchOffset(index, outgoing.size()));
                }
            }
        }
        for (int iteration = 0; iteration <= plan.nodes().size(); iteration++) {
            boolean changed = false;
            for (ProcessArtifactPlan.Flow flow : plan.flows()) {
                if ("end".equals(flow.source())) {
                    continue;
                }
                int sourceLane = lanes.getOrDefault(flow.source(), 0);
                if (sourceLane != 0 && lanes.getOrDefault(flow.target(), 0) == 0) {
                    lanes.put(flow.target(), sourceLane);
                    changed = true;
                }
            }
            if (!changed) {
                break;
            }
        }
        int endLane = plan.flows().stream().filter(flow -> "end".equals(flow.target()))
                .mapToInt(flow -> lanes.getOrDefault(flow.source(), 0)).max().orElse(0);
        lanes.put("end", endLane);
        return lanes;
    }

    private static int branchOffset(int index, int count) {
        if (count <= 1) {
            return 0;
        }
        if (count % 2 == 0) {
            return index < count / 2 ? index - count / 2 : index - count / 2 + 1;
        }
        return index - (count - 1) / 2;
    }

    private static boolean isGateway(ProcessArtifactPlan plan, String id) {
        return plan.nodes().stream().anyMatch(node -> node.id().equals(id) && "exclusive-gateway".equals(node.kind()));
    }

    private record Box(int x, int y, int width, int height) {
        int right() { return x + width; }
        int bottom() { return y + height; }
        int centerX() { return x + width / 2; }
        int centerY() { return y + height / 2; }
    }

    private record Layout(Map<String, Box> boxes, boolean acyclic) { }

    private record Point(int x, int y) { }

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
