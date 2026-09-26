package cn.com.crc.ems.finance.agent.evidence.application;

import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.evidence.domain.ProfessionalConfirmation;

/** 专业确认持久化端口。 */
public interface ProfessionalConfirmationRepository {

    Optional<ProfessionalConfirmation> findByFindingId(UUID findingId);

    void insert(ProfessionalConfirmation confirmation);
}
