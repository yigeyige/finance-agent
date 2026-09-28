package cn.com.crc.ems.finance.agent.harness.application;

/** 压缩结果不可信时阻断送模，并要求人工缩小范围。 */
public final class ContextCompressionException extends RuntimeException {

    public ContextCompressionException(String message) {
        super("Context compression rejected; manually reduce scope: " + message);
    }
}
