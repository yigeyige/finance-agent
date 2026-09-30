package cn.com.crc.ems.finance.agent.shared.web;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.crc.ems.finance.agent.diagnostics.DiagnosticContext;
import cn.com.crc.ems.finance.agent.diagnostics.DiagnosticLogSanitizer;
import cn.com.crc.ems.finance.agent.shared.application.DomainErrorCode;
import cn.com.crc.ems.finance.agent.shared.application.DomainException;

/** 统一将领域和未知异常映射为不泄漏内部细节的 HTTP 错误。 */
@Order
@RestControllerAdvice
public class GlobalApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalApiExceptionHandler.class);

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiErrorResponse> domain(DomainException exception) {
        return response(exception.errorCode());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> contract(IllegalArgumentException exception) {
        return response(DomainErrorCode.CONTRACT_ERROR);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> conflict(IllegalStateException exception) {
        return response(DomainErrorCode.STATE_CONFLICT);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> notFound(NoResourceFoundException exception) {
        return response(DomainErrorCode.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> unknown(Exception exception) {
        String traceId = traceId();
        logUnknownFailure(exception);
        return response(DomainErrorCode.INTERNAL_ERROR, traceId);
    }

    private static ResponseEntity<ApiErrorResponse> response(DomainErrorCode code) {
        return response(code, traceId());
    }

    private static ResponseEntity<ApiErrorResponse> response(DomainErrorCode code, String traceId) {
        return ResponseEntity.status(code.httpStatus()).body(ApiErrorResponse.of(code, traceId));
    }

    private static String traceId() {
        return DiagnosticContext.snapshot().getOrDefault(DiagnosticContext.TRACE_ID, UUID.randomUUID().toString());
    }

    private static void logUnknownFailure(Exception exception) {
        Map<String, String> fields = DiagnosticLogSanitizer.sanitize(Map.of(
                "event", "api_request_failed",
                "component", "global_exception_handler",
                "outcome", "failure",
                "exceptionType", exception.getClass().getSimpleName(),
                "rootCauseCategory", rootCauseCategory(exception),
                "rootCauseType", rootCauseType(exception),
                "databaseState", databaseState(exception)));
        LOGGER.atError()
                .addKeyValue("event", fields.get("event"))
                .addKeyValue("component", fields.get("component"))
                .addKeyValue("outcome", fields.get("outcome"))
                .addKeyValue("exceptionType", fields.get("exceptionType"))
                .addKeyValue("rootCauseCategory", fields.get("rootCauseCategory"))
                .addKeyValue("rootCauseType", fields.get("rootCauseType"))
                .addKeyValue("databaseState", fields.get("databaseState"))
                .log("Unhandled API request failed");
    }

    private static String rootCauseCategory(Throwable exception) {
        Throwable current = exception;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return switch (current.getClass().getSimpleName()) {
            case "InvalidDefinitionException" -> "json_type_definition";
            case "MismatchedInputException" -> "json_input_shape";
            default -> "other";
        };
    }

    /** 返回根因类型名用于受限分类，不记录异常消息、请求内容或 SQL。 */
    private static String rootCauseType(Throwable exception) {
        Throwable current = exception;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current.getClass().getSimpleName();
    }

    private static String databaseState(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof SQLException sqlException) {
                String state = usableSqlState(sqlException);
                return state == null ? "not_provided" : state;
            }
            current = current.getCause();
        }
        return "not_applicable";
    }

    /** 优先保留批量或事务异常链中首个有效 SQLSTATE，避免仅记录后续 25P02。 */
    private static String usableSqlState(SQLException exception) {
        SQLException current = exception;
        String fallback = null;
        while (current != null) {
            String state = current.getSQLState();
            if (state != null && state.matches("[A-Z0-9]{5}")) {
                if (!"25P02".equals(state)) {
                    return state;
                }
                fallback = state;
            }
            current = current.getNextException();
        }
        return fallback;
    }
}
