package cn.com.crc.ems.finance.agent.task.domain;

/**
 *
 * @Description: 表示调用状态已被其他执行者推进，当前更新不能覆盖原始结果。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public final class InvocationStateConflictException extends RuntimeException {

    public InvocationStateConflictException(String message) {
        super(message);
    }
}
