package cn.com.crc.ems.finance.agent.shared.web;

import java.util.UUID;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import cn.com.crc.ems.finance.agent.shared.application.DomainErrorCode;
import cn.com.crc.ems.finance.agent.shared.application.DomainException;

/** 统一将领域和未知异常映射为不泄漏内部细节的 HTTP 错误。 */
@Order
@RestControllerAdvice
public class GlobalApiExceptionHandler {

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
        return response(DomainErrorCode.INTERNAL_ERROR);
    }

    private static ResponseEntity<ApiErrorResponse> response(DomainErrorCode code) {
        return ResponseEntity.status(code.httpStatus()).body(ApiErrorResponse.of(code, UUID.randomUUID().toString()));
    }
}
