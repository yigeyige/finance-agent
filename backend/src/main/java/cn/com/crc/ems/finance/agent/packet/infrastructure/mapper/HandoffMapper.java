package cn.com.crc.ems.finance.agent.packet.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.packet.infrastructure.entity.HandoffEntity;

/** 决策包交接的 MyBatis-Plus Mapper。 */
@Mapper
public interface HandoffMapper extends BaseMapper<HandoffEntity> {
}
