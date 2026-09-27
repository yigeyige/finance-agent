package cn.com.crc.ems.finance.agent.task.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.task.infrastructure.entity.InvestigationTaskEntity;

/** 调查任务表的 MyBatis-Plus Mapper。 */
@Mapper
public interface InvestigationTaskMapper extends BaseMapper<InvestigationTaskEntity> {

    /** 在当前写事务中锁定任务行，供跨仓储用例建立稳定锁顺序。 */
    @Select("SELECT * FROM task.investigation_task "
            + "WHERE task_id = CAST(#{taskId} AS uuid) FOR UPDATE")
    InvestigationTaskEntity selectByIdForUpdate(@Param("taskId") String taskId);
}
