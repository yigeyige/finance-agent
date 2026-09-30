package cn.com.crc.ems.finance.agent.shared.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import cn.com.crc.ems.finance.agent.diagnostics.DiagnosticContext;

/**
 *
 * @Description: 校验全局异常响应复用请求追踪号且不改变对外安全错误契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-30 00:50:00
 */
class GlobalApiExceptionHandlerTest {

    @AfterEach
    void clearContext() {
        MDC.clear();
    }

    @Test
    void unknownFailureReusesRequestTraceIdAndKeepsSafeResponse() {
        MDC.put(DiagnosticContext.TRACE_ID, "trace-local-001");

        var response = new GlobalApiExceptionHandler().unknown(new IllegalStateException("password=not-exposed"));

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().traceId()).isEqualTo("trace-local-001");
        assertThat(response.getBody().message()).isEqualTo("服务暂时不可用");
    }
}
