package cn.com.crc.ems.finance.agent.evidence.application;

import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.evidence.domain.CoverageRecord;

/** 覆盖记录持久化端口。 */
public interface CoverageRecordRepository {

    List<CoverageRecord> findByTaskId(UUID taskId);

    void insert(CoverageRecord coverage);
}
