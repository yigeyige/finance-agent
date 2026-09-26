package cn.com.crc.ems.finance.agent.diagnostics;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class DiagnosticsControllerTest {

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new DiagnosticsController(new DiagnosticHealthService(List.of(
                    new DiagnosticProbe() {
                        @Override
                        public String name() {
                            return "database";
                        }

                        @Override
                        public DiagnosticComponentStatus check() {
                            return new DiagnosticComponentStatus("database", "UP", "test");
                        }
                    }))))
            .addFilters(new DiagnosticContextFilter())
            .build();

    @Test
    void returnsSafeStatusAndCorrelationContext() throws Exception {
        mockMvc.perform(get("/api/v1/diagnostics")
                        .header("X-Trace-Id", "trace-api-001")
                        .header("X-Invocation-Id", "inv-api-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components.database.reason").value("test"))
                .andExpect(jsonPath("$.context.traceId").value("trace-api-001"))
                .andExpect(jsonPath("$.context.invocationId").value("inv-api-001"));
    }
}
