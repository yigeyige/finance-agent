package cn.com.crc.ems.finance.agent.governance.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.governance.infrastructure.entity.OutboxEventEntity;

/** 本地 outbox 表 MyBatis-Plus Mapper。 */
@Mapper
public interface OutboxEventMapper extends BaseMapper<OutboxEventEntity> {
}
