package cn.com.crc.ems.finance.agent.packet.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.packet.infrastructure.entity.PacketRevisionEntity;

/** 已发布决策包修订的 MyBatis-Plus Mapper。 */
@Mapper
public interface PacketRevisionMapper extends BaseMapper<PacketRevisionEntity> {
}
