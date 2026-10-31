package cn.com.crc.ems.finance.agent.processartifact;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import cn.com.crc.ems.finance.agent.processartifact.domain.EmsProcessArtifactConverter;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactPlan;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;

/** 验证流程文件分层布局，节点不重叠且分支纵向分开。 */
class EmsProcessArtifactConverterLayoutTest {

    private static final String BRANCH = """
            {"name":"分支布局测试","nodes":[
              {"id":"draft","kind":"draft","name":"起草","assignee":""},
              {"id":"gateway","kind":"exclusive-gateway","name":"分支判断","assignee":""},
              {"id":"branchA","kind":"approve","name":"分支A","assignee":""},
              {"id":"branchB","kind":"approve","name":"分支B","assignee":""}
            ],"flows":[
              {"source":"start","target":"draft","condition":null},
              {"source":"draft","target":"gateway","condition":null},
              {"source":"gateway","target":"branchA","condition":{"variable":"code","operator":"==","value":"A"}},
              {"source":"gateway","target":"branchB","condition":{"variable":"code","operator":"!=","value":"A"}},
              {"source":"branchA","target":"end","condition":null},
              {"source":"branchB","target":"end","condition":null}
            ],"nodeParameters":[],"departmentApprovals":[],"approvalRules":[],"missingItems":[],"unsupported":[]}
            """;

    @Test
    void separatesBranchTargetsVertically() {
        ProcessArtifactPlan plan = ProcessArtifactPlan.fromJson(CanonicalJsonCodec.parse(BRANCH));
        JsonNode model = CanonicalJsonCodec.parse(new EmsProcessArtifactConverter().convert(plan).modelJson());
        List<JsonNode> shapes = new ArrayList<>();
        model.path("childShapes").forEach(shape -> {
            if (!shape.path("stencil").path("id").asText().equals("SequenceFlow")) {
                shapes.add(shape);
            }
        });

        JsonNode branchA = shape(shapes, "branchA");
        JsonNode branchB = shape(shapes, "branchB");
        JsonNode gateway = shape(shapes, "gateway");
        assertThat(x(branchA)).isEqualTo(x(branchB));
        assertThat(y(branchA)).isNotEqualTo(y(branchB));
        assertThat(width(gateway)).isEqualTo(40);
        assertThat(overlaps(shapes)).isEmpty();
    }

    private static JsonNode shape(List<JsonNode> shapes, String id) {
        return shapes.stream().filter(shape -> shape.path("properties").path("overrideid").asText().equals(id))
                .findFirst().orElseThrow();
    }

    private static int x(JsonNode shape) {
        return shape.path("bounds").path("upperLeft").path("x").asInt();
    }

    private static int y(JsonNode shape) {
        return shape.path("bounds").path("upperLeft").path("y").asInt();
    }

    private static int width(JsonNode shape) {
        return shape.path("bounds").path("lowerRight").path("x").asInt() - x(shape);
    }

    private static List<String> overlaps(List<JsonNode> shapes) {
        List<String> overlaps = new ArrayList<>();
        for (int i = 0; i < shapes.size(); i++) {
            for (int j = i + 1; j < shapes.size(); j++) {
                if (intersects(shapes.get(i), shapes.get(j))) {
                    overlaps.add(i + "/" + j);
                }
            }
        }
        return overlaps;
    }

    private static boolean intersects(JsonNode first, JsonNode second) {
        JsonNode a = first.path("bounds");
        JsonNode b = second.path("bounds");
        int aLeft = a.path("upperLeft").path("x").asInt();
        int aTop = a.path("upperLeft").path("y").asInt();
        int aRight = a.path("lowerRight").path("x").asInt();
        int aBottom = a.path("lowerRight").path("y").asInt();
        int bLeft = b.path("upperLeft").path("x").asInt();
        int bTop = b.path("upperLeft").path("y").asInt();
        int bRight = b.path("lowerRight").path("x").asInt();
        int bBottom = b.path("lowerRight").path("y").asInt();
        if (aLeft >= bRight) return false;
        if (bLeft >= aRight) return false;
        if (aTop >= bBottom) return false;
        if (bTop >= aBottom) return false;
        return true;
    }
}
