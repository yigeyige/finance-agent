package cn.com.crc.ems.finance.agent.packet.application;

import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.packet.domain.PacketRevision;

/** 已发布决策包修订的只追加、发布查询端口。 */
public interface PacketRevisionRepository {

    /** 按修订 ID 查询已保存快照。 */
    Optional<PacketRevision> findById(UUID packetRevisionId);

    /** 仅返回任务最新已发布修订，供只读查询使用。 */
    Optional<PacketRevision> findPublishedByTaskId(UUID taskId);

    /** 追加新的发布修订，不提供更新或删除方法。 */
    void insert(PacketRevision revision);
}
