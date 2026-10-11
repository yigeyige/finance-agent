package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.harness.domain.Checkpoint;

/**
 *
 * @Description: 按 task/user/session 隔离 checkpoint 的追加与查询端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface CheckpointRepository {

    /** 只按完整业务边界查询 checkpoint。 */
    Optional<Checkpoint> findById(UUID checkpointId, UUID taskId, UUID userId);

    /** 查询指定 session 的 checkpoint 历史。 */
    List<Checkpoint> findBySessionId(UUID sessionId, UUID taskId, UUID userId);

    /** 追加 checkpoint，不提供更新或删除。 */
    void insert(Checkpoint checkpoint);
}
