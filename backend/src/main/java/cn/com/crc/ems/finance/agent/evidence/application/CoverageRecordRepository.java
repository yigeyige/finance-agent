package cn.com.crc.ems.finance.agent.evidence.application;

import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.evidence.domain.CoverageRecord;

/**
 *
 * @Description: 覆盖记录持久化端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface CoverageRecordRepository {

    List<CoverageRecord> findByTaskId(UUID taskId);

    void insert(CoverageRecord coverage);
}
