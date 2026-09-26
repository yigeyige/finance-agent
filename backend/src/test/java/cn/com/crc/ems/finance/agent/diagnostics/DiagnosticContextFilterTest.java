package cn.com.crc.ems.finance.agent.diagnostics;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;

class DiagnosticContextFilterTest {

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void acceptsBoundedHeadersAndCleansMdcAfterRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Trace-Id", "trace-001");
        request.addHeader("X-Task-Id", "task-001");
        request.addHeader("X-Step-Id", "step-001");
        request.addHeader("X-Invocation-Id", "invocation-001");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> {
            assertThat(MDC.get(DiagnosticContext.TRACE_ID)).isEqualTo("trace-001");
            assertThat(MDC.get(DiagnosticContext.TASK_ID)).isEqualTo("task-001");
            assertThat(MDC.get(DiagnosticContext.STEP_ID)).isEqualTo("step-001");
            assertThat(MDC.get(DiagnosticContext.INVOCATION_ID)).isEqualTo("invocation-001");
        };

        new DiagnosticContextFilter().doFilter(request, response, chain);

        assertThat(response.getHeader("X-Trace-Id")).isEqualTo("trace-001");
        assertThat(response.getHeader("X-Invocation-Id")).isEqualTo("invocation-001");
        assertThat(MDC.get(DiagnosticContext.TRACE_ID)).isNull();
        assertThat(MDC.get(DiagnosticContext.TASK_ID)).isNull();
    }

    @Test
    void rejectsHeaderInjectionAndGeneratesSafeIds() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Trace-Id", "bad value\nforbidden");
        MockHttpServletResponse response = new MockHttpServletResponse();
        new DiagnosticContextFilter().doFilter(request, response, (req, res) -> {
        });

        assertThat(response.getHeader("X-Trace-Id")).matches("[A-Za-z0-9-]{36}");
        assertThat(response.getHeader("X-Invocation-Id")).matches("[A-Za-z0-9-]{36}");
    }
}
