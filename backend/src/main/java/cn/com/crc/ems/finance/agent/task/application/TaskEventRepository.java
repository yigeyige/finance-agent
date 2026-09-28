package cn.com.crc.ems.finance.agent.task.application;

import java.util.List;
import java.util.UUID;

/** 公开任务事件的追加与游标查询端口。 */
public interface TaskEventRepository {

    /** 追加事件并分配任务内单调序号。 */
    PublicTaskEvent append(UUID taskId, UUID userId, String eventType, String status,
            String publicReason, String resultReference, java.time.Instant occurredAt);

    /** 查询指定游标之后的任务事件。 */
    List<PublicTaskEvent> findAfter(UUID taskId, UUID userId, long afterSequence, int limit);
}
