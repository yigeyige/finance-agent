package cn.com.crc.ems.finance.agent.businesspacket.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.businesspacket.infrastructure.entity.BusinessDecisionPacketEntity;

/**
 *
 * @Description: 业务决策包表的 MyBatis-Plus Mapper。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-24 10:00:00
 */
@Mapper
public interface BusinessDecisionPacketMapper extends BaseMapper<BusinessDecisionPacketEntity> {
}
