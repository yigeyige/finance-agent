package cn.com.crc.ems.finance.agent.task.domain;

/**
 *
 * @Description: 表示任务状态不满足权威状态转移表或必要守卫。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 22:40:00
 */
public final class InvalidTaskTransitionException extends IllegalStateException {

    public InvalidTaskTransitionException(String message) {
        super(message);
    }
}
