package cn.com.crc.ems.finance.agent.evidence.application;

import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.evidence.domain.FindingEvidence;

/**
 *
 * @Description: 判断证据引用持久化端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface FindingEvidenceRepository {

    List<FindingEvidence> findByFindingId(UUID findingId);

    void insert(FindingEvidence evidence);
}
