package cn.com.crc.ems.finance.agent.task.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.task.infrastructure.entity.StepApprovalEntity;

/** 步骤审批表的 MyBatis-Plus Mapper。 */
@Mapper
public interface StepApprovalMapper extends BaseMapper<StepApprovalEntity> {

    @Update("UPDATE task.step_approval SET status = 'STALE', updated_by = CAST(#{actorUserId} AS uuid), "
            + "updated_at = #{updatedAt} WHERE scope_revision_id = CAST(#{scopeRevisionId} AS uuid) "
            + "AND status = 'ACTIVE' AND step_id IN (SELECT step_id FROM task.investigation_step "
            + "WHERE task_id = CAST(#{taskId} AS uuid))")
    int markStaleByScopeRevision(@Param("taskId") String taskId, @Param("scopeRevisionId") String scopeRevisionId,
            @Param("actorUserId") String actorUserId, @Param("updatedAt") java.time.Instant updatedAt);
}
