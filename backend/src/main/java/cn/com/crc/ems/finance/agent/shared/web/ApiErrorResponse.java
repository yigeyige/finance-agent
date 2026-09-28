package cn.com.crc.ems.finance.agent.shared.web;

import cn.com.crc.ems.finance.agent.shared.application.DomainErrorCode;

/** 对外错误响应，仅保留稳定错误目录和追踪标识。 */
public record ApiErrorResponse(String code, String message, String retryClass, String traceId) {

    public static ApiErrorResponse of(DomainErrorCode errorCode, String traceId) {
        return new ApiErrorResponse(errorCode.name(), errorCode.safeMessage(), errorCode.retryClass().name(), traceId);
    }
}
