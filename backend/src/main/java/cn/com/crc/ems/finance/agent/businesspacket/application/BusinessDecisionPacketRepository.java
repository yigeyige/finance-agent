package cn.com.crc.ems.finance.agent.businesspacket.application;

import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.businesspacket.domain.BusinessDecisionPacket;

/**
 *
 * @Description: 业务决策包持久化端口，按修订号追加并读取指定修订。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-24 10:00:00
 */
public interface BusinessDecisionPacketRepository {

    /** 读取任务最新修订，不存在时返回空。 */
    Optional<BusinessDecisionPacket> findLatestByTaskId(UUID taskId);

    /** 按任务与修订号读取决策包，不存在时返回空。 */
    Optional<BusinessDecisionPacket> findByTaskIdAndRevision(UUID taskId, int revision);

    /** 追加保存一份决策包修订。 */
    void insert(BusinessDecisionPacket packet);
}
