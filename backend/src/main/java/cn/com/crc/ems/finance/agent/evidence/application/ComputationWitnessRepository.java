package cn.com.crc.ems.finance.agent.evidence.application;

import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.evidence.domain.ComputationWitness;

/**
 *
 * @Description: 计算见证持久化端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface ComputationWitnessRepository {

    Optional<ComputationWitness> findByFindingId(UUID findingId);

    void insert(ComputationWitness witness);
}
