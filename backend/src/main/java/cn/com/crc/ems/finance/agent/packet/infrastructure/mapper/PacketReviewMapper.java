package cn.com.crc.ems.finance.agent.packet.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.packet.infrastructure.entity.PacketReviewEntity;

/** 决策包复核的 MyBatis-Plus Mapper。 */
@Mapper
public interface PacketReviewMapper extends BaseMapper<PacketReviewEntity> {
}
