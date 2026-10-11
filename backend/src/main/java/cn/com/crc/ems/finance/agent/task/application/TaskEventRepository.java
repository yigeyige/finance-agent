package cn.com.crc.ems.finance.agent.task.application;

import java.util.List;
import java.util.UUID;

/**
 *
 * @Description: 公开任务事件的追加与游标查询端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface TaskEventRepository {

    /** 追加事件并分配任务内单调序号。 */
    PublicTaskEvent append(UUID taskId, UUID userId, String eventType, String status,
            String publicReason, String resultReference, java.time.Instant occurredAt);

    /** 查询指定游标之后的任务事件。 */
    List<PublicTaskEvent> findAfter(UUID taskId, UUID userId, long afterSequence, int limit);
}
