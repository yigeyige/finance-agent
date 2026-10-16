package cn.com.crc.ems.finance.agent.shared.application;

/**
 *
 * @Description: 稳定领域错误目录，禁止直接暴露底层异常文本。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public enum DomainErrorCode {
    CONTRACT_ERROR(400, RetryClass.NOT_RETRYABLE, "请求不满足业务契约"),
    STATE_CONFLICT(409, RetryClass.NEW_APPROVAL_REQUIRED, "当前业务状态已变化"),
    NOT_FOUND(404, RetryClass.NOT_RETRYABLE, "业务对象不存在"),
    FORBIDDEN(403, RetryClass.NOT_RETRYABLE, "无权执行该业务动作"),
    CONFIGURATION_UNAVAILABLE(503, RetryClass.NOT_RETRYABLE, "凭据加密主密钥未配置，请联系平台管理员"),
    DATA_EXPLORATION_UNAVAILABLE(503, RetryClass.NOT_RETRYABLE, "数据探索暂时不可用，请检查 EMS 数据库连接"),
    AUDIT_WRITE_FAILED(503, RetryClass.MANUAL_RECONCILIATION, "审计不可用，动作未完成"),
    INTERNAL_ERROR(500, RetryClass.NOT_RETRYABLE, "服务暂时不可用");

    private final int httpStatus;
    private final RetryClass retryClass;
    private final String safeMessage;

    DomainErrorCode(int httpStatus, RetryClass retryClass, String safeMessage) {
        this.httpStatus = httpStatus;
        this.retryClass = retryClass;
        this.safeMessage = safeMessage;
    }

    public int httpStatus() { return httpStatus; }
    public RetryClass retryClass() { return retryClass; }
    public String safeMessage() { return safeMessage; }
}
