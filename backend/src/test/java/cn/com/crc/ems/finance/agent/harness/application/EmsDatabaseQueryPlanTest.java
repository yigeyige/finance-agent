package cn.com.crc.ems.finance.agent.harness.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;

/** 验证查询计划的依赖、绑定和注入防护。 */
class EmsDatabaseQueryPlanTest {

    @Test
    void parsesDependentPlan() {
        String json = """
                {"steps":[
                  {"id":"process","purpose":"定位流程","sql":"SELECT id_ AS procdef_id FROM act_re_procdef WHERE key_ = :key","dependsOn":[],"bindings":{"key":{"value":"8号流程"}}},
                  {"id":"nodes","purpose":"查询节点","sql":"SELECT act_id FROM psc_act_config WHERE procdef_id = :procdefId","dependsOn":["process"],"bindings":{"procdefId":{"stepId":"process","column":"procdef_id"}}}
                ]}
                """;

        EmsDatabaseQueryPlan plan = EmsDatabaseQueryPlan.fromJson(CanonicalJsonCodec.parse(json));

        assertThat(plan.steps()).hasSize(2);
        assertThat(plan.steps().get(1).bindings()).containsKey("procdefId");
        assertThat(plan.steps().get(1).bindings().get("procdefId").stepId()).isEqualTo("process");
    }

    @Test
    void rejectsStringInterpolation() {
        String json = """
                {"steps":[{"id":"process","purpose":"定位流程","sql":"SELECT * FROM t WHERE id = ${id}","dependsOn":[],"bindings":{}}]}
                """;

        assertThatThrownBy(() -> EmsDatabaseQueryPlan.fromJson(CanonicalJsonCodec.parse(json)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsBindingStepOutsideDependsOn() {
        String json = """
                {"steps":[
                  {"id":"process","purpose":"定位流程","sql":"SELECT id_ AS procdef_id FROM t","dependsOn":[],"bindings":{}},
                  {"id":"nodes","purpose":"查询节点","sql":"SELECT act_id FROM t2 WHERE procdef_id = :procdefId","dependsOn":[],"bindings":{"procdefId":{"stepId":"process","column":"procdef_id"}}}
                ]}
                """;

        assertThatThrownBy(() -> EmsDatabaseQueryPlan.fromJson(CanonicalJsonCodec.parse(json)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsCycles() {
        String json = """
                {"steps":[
                  {"id":"a","purpose":"A","sql":"SELECT 1","dependsOn":["b"],"bindings":{}},
                  {"id":"b","purpose":"B","sql":"SELECT 1","dependsOn":["a"],"bindings":{}}
                ]}
                """;

        assertThatThrownBy(() -> EmsDatabaseQueryPlan.fromJson(CanonicalJsonCodec.parse(json)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
