package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 对外仅承载安全说明的模型交互失败。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public class BusinessModelException extends RuntimeException {

    public BusinessModelException(String message) {
        super(message);
    }

    public BusinessModelException(String message, Throwable cause) {
        super(message, cause);
    }
}
