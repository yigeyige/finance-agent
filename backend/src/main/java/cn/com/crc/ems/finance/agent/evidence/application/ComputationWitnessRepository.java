package cn.com.crc.ems.finance.agent.evidence.application;

import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.evidence.domain.ComputationWitness;

/** 计算见证持久化端口。 */
public interface ComputationWitnessRepository {

    Optional<ComputationWitness> findByFindingId(UUID findingId);

    void insert(ComputationWitness witness);
}
