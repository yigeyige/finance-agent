package cn.com.crc.ems.finance.agent.scenario.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 *
 * @Description: 校验 BpmnStructureProjector 同时支持标准 BPMN XML 与 EMS Activiti model JSON。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-04 11:40:00
 */
class BpmnStructureProjectorTest {

    private final BpmnStructureProjector projector = new BpmnStructureProjector();

    @Test
    void projectsActivitiModelJsonWithoutXmlParseFailure() {
        String json = """
                {"properties":{"name":"中艺深圳-退还按金"},
                 "childShapes":[
                  {"resourceId":"sid-start","stencil":{"id":"StartNoneEvent"},"properties":{"overrideid":"start","name":"开始"},"outgoing":[{"resourceId":"sid-f1"}],"bounds":{"upperLeft":{"x":0,"y":0},"lowerRight":{"x":40,"y":40}}},
                  {"resourceId":"sid-draft","stencil":{"id":"UserTask"},"properties":{"overrideid":"act_010","name":"起草"},"outgoing":[{"resourceId":"sid-f2"}],"bounds":{"upperLeft":{"x":100,"y":0},"lowerRight":{"x":220,"y":60}}},
                  {"resourceId":"sid-gw","stencil":{"id":"ExclusiveGateway"},"properties":{"overrideid":"gateway_role","name":"起草角色分支"},"outgoing":[{"resourceId":"sid-f3"}],"bounds":{"upperLeft":{"x":300,"y":0},"lowerRight":{"x":400,"y":100}}},
                  {"resourceId":"sid-end","stencil":{"id":"EndNoneEvent"},"properties":{"overrideid":"end","name":"结束"},"outgoing":[],"bounds":{"upperLeft":{"x":500,"y":0},"lowerRight":{"x":540,"y":40}}},
                  {"resourceId":"sid-f1","stencil":{"id":"SequenceFlow"},"target":{"resourceId":"sid-draft"},"properties":{"conditionsequenceflow":""}},
                  {"resourceId":"sid-f2","stencil":{"id":"SequenceFlow"},"target":{"resourceId":"sid-gw"},"properties":{"conditionsequenceflow":"${roleCode=='BMFZR'}"}},
                  {"resourceId":"sid-f3","stencil":{"id":"SequenceFlow"},"target":{"resourceId":"sid-end"},"properties":{"conditionsequenceflow":""}}
                 ]}
                """;

        String projected = projector.project(json);

        assertThat(projected).contains("\"processName\":\"中艺深圳-退还按金\"");
        assertThat(projected).contains("\"id\":\"act_010\"");
        assertThat(projected).contains("\"type\":\"exclusiveGateway\"");
        assertThat(projected).contains("\"source\":\"act_010\"");
        assertThat(projected).contains("\"target\":\"gateway_role\"");
        assertThat(projected).contains("roleCode");
    }

    @Test
    void projectsStandardBpmnXml() {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL">
                  <bpmn:process id="p1" name="测试流程">
                    <bpmn:startEvent id="start"/>
                    <bpmn:userTask id="act_010" name="起草"/>
                    <bpmn:endEvent id="end"/>
                    <bpmn:sequenceFlow id="f1" sourceRef="start" targetRef="act_010"/>
                    <bpmn:sequenceFlow id="f2" sourceRef="act_010" targetRef="end"/>
                  </bpmn:process>
                </bpmn:definitions>
                """;

        String projected = projector.project(xml);

        assertThat(projected).contains("\"processName\":\"测试流程\"");
        assertThat(projected).contains("\"id\":\"act_010\"");
        assertThat(projected).contains("\"source\":\"start\"");
        assertThat(projected).contains("\"target\":\"act_010\"");
    }
}
