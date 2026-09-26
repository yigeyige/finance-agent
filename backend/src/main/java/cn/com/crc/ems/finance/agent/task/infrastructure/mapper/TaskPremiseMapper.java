package cn.com.crc.ems.finance.agent.task.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.task.infrastructure.entity.TaskPremiseEntity;

/** 任务前提表的 MyBatis-Plus Mapper。 */
@Mapper
public interface TaskPremiseMapper extends BaseMapper<TaskPremiseEntity> {
}
