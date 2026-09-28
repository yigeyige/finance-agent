package cn.com.crc.ems.finance.agent.evidence.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.evidence.domain.Finding;
import cn.com.crc.ems.finance.agent.evidence.domain.FindingStatus;

/** 判断结果持久化端口。 */
public interface FindingRepository {

    Optional<Finding> findById(UUID findingId);

    List<Finding> findByTaskId(UUID taskId);

    void insert(Finding finding);

    boolean updateStatus(UUID findingId, FindingStatus status, java.util.UUID updatedBy,
            java.time.Instant updatedAt);
}
