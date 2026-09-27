package cn.com.crc.ems.finance.agent.packet.application;

import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.packet.domain.Handoff;

/** 决策包交接记录的追加与查询端口。 */
public interface HandoffRepository {

    /** 按交接 ID 查询。 */
    Optional<Handoff> findById(UUID handoffId);

    /** 查询修订对应的交接记录。 */
    Optional<Handoff> findByPacketRevisionId(UUID packetRevisionId);

    /** 追加交接记录。 */
    void insert(Handoff handoff);
}
