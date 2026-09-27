package cn.com.crc.ems.finance.agent.packet.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.packet.infrastructure.entity.PacketDraftEntity;

/** 决策包草稿的 MyBatis-Plus Mapper。 */
@Mapper
public interface PacketDraftMapper extends BaseMapper<PacketDraftEntity> {
}
