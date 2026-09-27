package cn.com.crc.ems.finance.agent.task.domain;

import java.util.Objects;

/** 已通过权威状态机校验的任务状态转移。 */
public record TaskTransition(TaskStatus fromStatus, TaskStatus toStatus, String reason) {

    public TaskTransition {
        Objects.requireNonNull(fromStatus, "fromStatus");
        Objects.requireNonNull(toStatus, "toStatus");
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("transition reason must not be blank");
        }
    }
}
