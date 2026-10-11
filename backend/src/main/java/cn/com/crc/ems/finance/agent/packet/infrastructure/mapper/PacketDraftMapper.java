package cn.com.crc.ems.finance.agent.packet.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.packet.infrastructure.entity.PacketDraftEntity;

/**
 *
 * @Description: 决策包草稿的 MyBatis-Plus Mapper。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Mapper
public interface PacketDraftMapper extends BaseMapper<PacketDraftEntity> {

    @Update("UPDATE packet.packet_draft SET status = 'STALE', updated_by = CAST(#{actorUserId} AS uuid), "
            + "updated_at = #{updatedAt} WHERE task_id = CAST(#{taskId} AS uuid) "
            + "AND scope_revision_id = CAST(#{scopeRevisionId} AS uuid) AND status = 'CURRENT'")
    int markStaleByScopeRevision(@Param("taskId") String taskId, @Param("scopeRevisionId") String scopeRevisionId,
            @Param("actorUserId") String actorUserId, @Param("updatedAt") java.time.Instant updatedAt);
}
