package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 压缩结果不可信时阻断送模，并要求人工缩小范围。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public final class ContextCompressionException extends RuntimeException {

    public ContextCompressionException(String message) {
        super("Context compression rejected; manually reduce scope: " + message);
    }
}
