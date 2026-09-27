package cn.com.crc.ems.finance.agent.task.domain;

/** 表示调用状态已被其他执行者推进，当前更新不能覆盖原始结果。 */
public final class InvocationStateConflictException extends RuntimeException {

    public InvocationStateConflictException(String message) {
        super(message);
    }
}
