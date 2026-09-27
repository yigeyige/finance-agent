package cn.com.crc.ems.finance.agent.harness.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.harness.infrastructure.entity.CheckpointEntity;

/** Harness checkpoint 的 MyBatis-Plus Mapper。 */
@Mapper
public interface CheckpointMapper extends BaseMapper<CheckpointEntity> {
}
