package cn.com.crc.ems.finance.agent.evidence.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.evidence.domain.FactSnapshot;

/**
 *
 * @Description: 事实快照持久化端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface FactSnapshotRepository {

    Optional<FactSnapshot> findById(UUID snapshotId);

    List<FactSnapshot> findByTaskId(UUID taskId);

    void insert(FactSnapshot snapshot);
}
