package cn.com.crc.ems.finance.agent.task.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.task.infrastructure.entity.InvestigationStepEntity;

/** 调查步骤表的 MyBatis-Plus Mapper。 */
@Mapper
public interface InvestigationStepMapper extends BaseMapper<InvestigationStepEntity> {
}
