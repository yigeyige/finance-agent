package cn.com.crc.ems.finance.agent.scenario.application;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;

/**
 *
 * @Description: 把 BPMN 结构投影为节点/流转/条件/坐标骨架的紧凑 JSON，供调查阶段参考，不返回原文。
 *               支持标准 BPMN XML 与 EMS Activiti model JSON（扩展名为 .xml、内容为 childShapes JSON）。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-04 11:30:00
 */
public final class BpmnStructureProjector {

    private static final int MAX_OUTPUT = 20_000;
    private static final String[] NODE_TYPES = {"startEvent", "endEvent", "userTask", "exclusiveGateway"};

    /** 按内容而非扩展名选择投影方式：JSON 走 Activiti model 投影，其余走 BPMN XML 投影。 */
    public String project(String content) {
        String sanitized = content == null ? "" : content.replace("\uFEFF", "").stripLeading();
        if (sanitized.startsWith("{")) {
            try {
                return projectModelJson(sanitized);
            } catch (RuntimeException exception) {
                // 非 Activiti model JSON 时回退 XML 解析，由 XML 分支给出最终失败原因。
            }
        }
        return projectXml(sanitized);
    }

    private String projectXml(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setExpandEntityReferences(false);
            String sanitized = xml == null ? "" : xml.replace("\uFEFF", "").stripLeading();
            int firstTag = sanitized.indexOf('<');
            if (firstTag > 0) {
                sanitized = sanitized.substring(firstTag);
            }
            Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(sanitized)));
            ObjectNode root = JsonNodeFactory.instance.objectNode();
            Element process = first(document, "process");
            root.put("processName", process == null ? "" : attribute(process, "name"));
            Map<String, String> bounds = bounds(document);
            ArrayNode nodes = root.putArray("nodes");
            for (String type : NODE_TYPES) {
                for (Element element : elements(document, type)) {
                    ObjectNode node = nodes.addObject();
                    String id = attribute(element, "id");
                    node.put("id", id);
                    node.put("type", type);
                    node.put("name", attribute(element, "name"));
                    String box = bounds.get(id);
                    if (box != null) {
                        node.put("bounds", box);
                    }
                }
            }
            ArrayNode flows = root.putArray("flows");
            for (Element element : elements(document, "sequenceFlow")) {
                ObjectNode flow = flows.addObject();
                flow.put("source", attribute(element, "sourceRef"));
                flow.put("target", attribute(element, "targetRef"));
                flow.put("condition", text(element, "conditionExpression"));
            }
            return truncate(root.toString());
        } catch (Exception exception) {
            throw new IllegalStateException("BPMN 结构投影解析失败", exception);
        }
    }

    /**
     * 投影 EMS Activiti model JSON。该格式的 SequenceFlow.source 常为空，需要从节点 outgoing 反推来源。
     */
    private String projectModelJson(String json) {
        JsonNode document = CanonicalJsonCodec.parse(json);
        ObjectNode root = JsonNodeFactory.instance.objectNode();
        root.put("processName", text(document.path("properties").path("name")));
        ArrayNode nodes = root.putArray("nodes");
        Map<String, String> nodeIdByResource = new HashMap<>();
        Map<String, String> flowSource = new LinkedHashMap<>();
        Map<String, String> flowTarget = new LinkedHashMap<>();
        Map<String, String> flowCondition = new LinkedHashMap<>();
        for (JsonNode shape : document.path("childShapes")) {
            String stencil = text(shape.path("stencil").path("id"));
            String resourceId = text(shape.path("resourceId"));
            String type = nodeType(stencil);
            if (type != null) {
                String overrideId = text(shape.path("properties").path("overrideid"));
                String id = overrideId.isBlank() ? resourceId : overrideId;
                if (!resourceId.isBlank()) {
                    nodeIdByResource.put(resourceId, id);
                }
                if (!id.isBlank()) {
                    ObjectNode node = nodes.addObject();
                    node.put("id", id);
                    node.put("type", type);
                    node.put("name", text(shape.path("properties").path("name")));
                    String box = bounds(shape);
                    if (!box.isBlank()) {
                        node.put("bounds", box);
                    }
                    for (JsonNode outgoing : shape.path("outgoing")) {
                        String flowId = text(outgoing.path("resourceId"));
                        if (!flowId.isBlank()) {
                            flowSource.putIfAbsent(flowId, id);
                        }
                    }
                }
            } else if ("SequenceFlow".equals(stencil) && !resourceId.isBlank()) {
                String targetResource = text(shape.path("target").path("resourceId"));
                if (targetResource.isBlank()) {
                    JsonNode outgoing = shape.path("outgoing");
                    if (outgoing.isArray() && !outgoing.isEmpty()) {
                        targetResource = text(outgoing.get(0).path("resourceId"));
                    }
                }
                flowTarget.put(resourceId, targetResource);
                flowCondition.put(resourceId, text(shape.path("properties").path("conditionsequenceflow")));
            }
        }
        ArrayNode flows = root.putArray("flows");
        for (Map.Entry<String, String> entry : flowTarget.entrySet()) {
            String targetResource = entry.getValue();
            ObjectNode flow = flows.addObject();
            flow.put("source", flowSource.getOrDefault(entry.getKey(), ""));
            flow.put("target", nodeIdByResource.getOrDefault(targetResource, targetResource));
            flow.put("condition", flowCondition.getOrDefault(entry.getKey(), ""));
        }
        return truncate(root.toString());
    }

    private static String nodeType(String stencil) {
        return switch (stencil) {
            case "StartNoneEvent", "StartEvent", "StartTimerEvent", "StartMessageEvent", "StartSignalEvent" ->
                    "startEvent";
            case "EndNoneEvent", "EndEvent", "EndTerminateEvent", "EndErrorEvent" -> "endEvent";
            case "UserTask", "Task" -> "userTask";
            case "ExclusiveGateway" -> "exclusiveGateway";
            default -> null;
        };
    }

    private static String bounds(JsonNode shape) {
        JsonNode upper = shape.path("bounds").path("upperLeft");
        JsonNode lower = shape.path("bounds").path("lowerRight");
        if (!upper.isObject() || !lower.isObject()) {
            return "";
        }
        double x = upper.path("x").asDouble();
        double y = upper.path("y").asDouble();
        double width = lower.path("x").asDouble() - x;
        double height = lower.path("y").asDouble() - y;
        return number(x) + "," + number(y) + "," + number(width) + "," + number(height);
    }

    private static String number(double value) {
        return BigDecimal.valueOf(Math.round(value * 100.0) / 100.0).stripTrailingZeros().toPlainString();
    }

    private static String text(JsonNode node) {
        return node == null || node.isNull() || node.isMissingNode() ? "" : node.asText("");
    }

    private static String truncate(String json) {
        return json.length() <= MAX_OUTPUT ? json : json.substring(0, MAX_OUTPUT) + "...（结构投影已截断）";
    }

    private static Map<String, String> bounds(Document document) {
        Map<String, String> result = new HashMap<>();
        for (Element shape : elements(document, "BPMNShape")) {
            Element box = firstChild(shape, "Bounds");
            if (box != null) {
                result.put(attribute(shape, "bpmnElement"), attribute(box, "x") + "," + attribute(box, "y") + ","
                        + attribute(box, "width") + "," + attribute(box, "height"));
            }
        }
        return result;
    }

    private static NodeList local(Document document, String name) {
        return document.getElementsByTagNameNS("*", name);
    }

    private static Iterable<Element> elements(Document document, String name) {
        NodeList list = local(document, name);
        java.util.List<Element> result = new java.util.ArrayList<>();
        for (int index = 0; index < list.getLength(); index++) {
            result.add((Element) list.item(index));
        }
        return result;
    }

    private static Element first(Document document, String name) {
        NodeList list = local(document, name);
        return list.getLength() == 0 ? null : (Element) list.item(0);
    }

    private static Element firstChild(Element parent, String name) {
        NodeList children = parent.getChildNodes();
        for (int index = 0; index < children.getLength(); index++) {
            if (children.item(index) instanceof Element element && name.equals(element.getLocalName())) {
                return element;
            }
        }
        return null;
    }

    private static String attribute(Element element, String name) {
        String value = element.getAttribute(name);
        return value == null ? "" : value;
    }

    private static String text(Element parent, String name) {
        Element child = firstChild(parent, name);
        return child == null ? "" : child.getTextContent().trim();
    }
}
