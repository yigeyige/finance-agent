package cn.com.crc.ems.finance.agent.evidence.application;

import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.evidence.domain.FindingEvidence;

/** 判断证据引用持久化端口。 */
public interface FindingEvidenceRepository {

    List<FindingEvidence> findByFindingId(UUID findingId);

    void insert(FindingEvidence evidence);
}
