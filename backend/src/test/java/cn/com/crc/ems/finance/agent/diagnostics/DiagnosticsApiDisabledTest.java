package cn.com.crc.ems.finance.agent.diagnostics;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import cn.com.crc.ems.finance.agent.FinanceAgentApplication;

@SpringBootTest(classes = FinanceAgentApplication.class,
        properties = "finance-agent.diagnostics.api-enabled=false")
class DiagnosticsApiDisabledTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void returnsNotFoundWhenDiagnosticsApiIsNotExplicitlyEnabled() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api/v1/diagnostics"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.components").doesNotExist());
    }
}
