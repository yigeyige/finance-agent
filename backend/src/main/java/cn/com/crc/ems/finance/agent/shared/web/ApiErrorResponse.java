package cn.com.crc.ems.finance.agent.shared.web;

import cn.com.crc.ems.finance.agent.shared.application.DomainErrorCode;

/**
 *
 * @Description: 对外错误响应，仅保留稳定错误目录和追踪标识。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ApiErrorResponse(String code, String message, String retryClass, String traceId) {

    public static ApiErrorResponse of(DomainErrorCode errorCode, String traceId) {
        return new ApiErrorResponse(errorCode.name(), errorCode.safeMessage(), errorCode.retryClass().name(), traceId);
    }

    /**
     *
     * @description: 以自定义安全文案和错误码构造错误响应，用于非枚举的业务拒绝场景。
     *
     * @param: code 稳定的业务错误码
     * @param: message 已脱敏的中文提示
     * @param: retryClass 重试类别
     * @param: traceId 当前请求追踪标识
     * @return: 对外错误响应
     * @author: yigeyige99
     */
    public static ApiErrorResponse of(String code, String message, String retryClass, String traceId) {
        return new ApiErrorResponse(code, message, retryClass, traceId);
    }
}
