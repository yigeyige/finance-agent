package cn.com.crc.ems.finance.agent.diagnostics;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Adds bounded correlation IDs to MDC and response headers for every HTTP request. */
@Component
public final class DiagnosticContextFilter extends OncePerRequestFilter {

    private static final Pattern SAFE_ID = Pattern.compile("[A-Za-z0-9][A-Za-z0-9._:-]{0,99}");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String traceId = acceptedOrGenerated(request.getHeader("X-Trace-Id"));
        String invocationId = acceptedOrGenerated(request.getHeader("X-Invocation-Id"));
        put(DiagnosticContext.TRACE_ID, traceId);
        put(DiagnosticContext.INVOCATION_ID, invocationId);
        putIfAccepted(DiagnosticContext.TASK_ID, request.getHeader("X-Task-Id"));
        putIfAccepted(DiagnosticContext.STEP_ID, request.getHeader("X-Step-Id"));
        response.setHeader("X-Trace-Id", traceId);
        response.setHeader("X-Invocation-Id", invocationId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(DiagnosticContext.TRACE_ID);
            MDC.remove(DiagnosticContext.TASK_ID);
            MDC.remove(DiagnosticContext.STEP_ID);
            MDC.remove(DiagnosticContext.INVOCATION_ID);
        }
    }

    private static String acceptedOrGenerated(String value) {
        return value != null && SAFE_ID.matcher(value).matches() ? value : DiagnosticContext.generatedId();
    }

    private static void putIfAccepted(String key, String value) {
        if (value != null && SAFE_ID.matcher(value).matches()) {
            put(key, value);
        }
    }

    private static void put(String key, String value) {
        MDC.put(key, value);
    }
}
