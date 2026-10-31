package cn.com.crc.ems.finance.agent.scenario.application;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @Description: 把 BPMN XML 投影为节点/流转/条件/坐标骨架的紧凑 JSON，供调查阶段参考，不返回原文。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 18:00:00
 */
public final class BpmnStructureProjector {

    private static final int MAX_OUTPUT = 20_000;
    private static final String[] NODE_TYPES = {"startEvent", "endEvent", "userTask", "exclusiveGateway"};

    /** 解析 XML 并返回结构投影；解析失败时抛出安全异常。 */
    public String project(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setExpandEntityReferences(false);
            Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
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
            String json = root.toString();
            return json.length() <= MAX_OUTPUT ? json
                    : json.substring(0, MAX_OUTPUT) + "...（结构投影已截断）";
        } catch (Exception exception) {
            throw new IllegalStateException("BPMN 结构投影解析失败", exception);
        }
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
