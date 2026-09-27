package cn.com.crc.ems.finance.agent.task.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.task.infrastructure.entity.InvestigationStepEntity;

/** 调查步骤表的 MyBatis-Plus Mapper。 */
@Mapper
public interface InvestigationStepMapper extends BaseMapper<InvestigationStepEntity> {

    /** 按稳定顺序领取一个可执行步骤，跳过已被其他 Worker 锁定的行。 */
    @Select("SELECT s.* FROM task.investigation_step s "
            + "JOIN task.investigation_task t ON t.task_id = s.task_id "
            + "WHERE t.status = #{taskStatus} AND s.status = #{stepStatus} "
            + "ORDER BY s.created_at ASC, s.step_id ASC "
            + "LIMIT 1 FOR UPDATE OF s, t SKIP LOCKED")
    InvestigationStepEntity selectNextForUpdateSkipLocked(@Param("taskStatus") String taskStatus,
            @Param("stepStatus") String stepStatus);
}
