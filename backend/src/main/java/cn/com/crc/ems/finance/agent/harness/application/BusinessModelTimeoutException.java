package cn.com.crc.ems.finance.agent.harness.application;

/**
 * @Description: 模型调用超时，供接口层映射为 504。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 15:10:00
 */
public class BusinessModelTimeoutException extends BusinessModelException {

    public BusinessModelTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
