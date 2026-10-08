package cn.com.crc.ems.finance.agent.harness.application;

/** 对外仅承载安全说明的模型交互失败。 */
public final class BusinessModelException extends RuntimeException {

    public BusinessModelException(String message) {
        super(message);
    }

    public BusinessModelException(String message, Throwable cause) {
        super(message, cause);
    }
}
