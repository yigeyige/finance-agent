package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 模型输出不符合已约定结构；消息只包含安全字段名和校验原因，不携带模型原文。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 17:30:00
 */
public final class BusinessModelOutputException extends BusinessModelException {

    public BusinessModelOutputException(String message) {
        super(message);
    }

    public BusinessModelOutputException(String message, Throwable cause) {
        super(message, cause);
    }
}
