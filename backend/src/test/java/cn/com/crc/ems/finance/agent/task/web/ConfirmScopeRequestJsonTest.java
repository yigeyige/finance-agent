package cn.com.crc.ems.finance.agent.task.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.json.JsonMapper;

/** 验证核心纵向切片的范围 JSON 可绑定为 HTTP 请求。 */
class ConfirmScopeRequestJsonTest {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Test
    void acceptsCoreSyntheticVerticalSliceScope() throws Exception {
        ConfirmScopeRequest request = jsonMapper.readValue("""
                {
                  "systemCode":"FIN-SYN",
                  "objectSelector":{"employeeKey":"EMP-SYN-001"},
                  "effectiveTimeRange":{"from":"2026-01-01T00:00:00Z","to":"2026-12-31T23:59:59Z"},
                  "includedPaths":["workflow.reference","role.reference"],
                  "excludedPaths":["credential.secret"],
                  "allowedReadFields":["reference.id","reference.classification"],
                  "allowedRetainFields":["reference.id","reference.classification"],
                  "allowedModelFields":["reference.id","reference.classification"],
                  "schemaVersion":"scope-v1"
                }
                """, ConfirmScopeRequest.class);

        assertThat(request.systemCode()).isEqualTo("FIN-SYN");
        assertThat(request.objectSelector().path("employeeKey").asText()).isEqualTo("EMP-SYN-001");
        assertThat(request.allowedModelFields()).hasSize(2);
    }
}
