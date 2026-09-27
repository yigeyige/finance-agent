package cn.com.crc.ems.finance.agent.governance.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.governance.infrastructure.entity.AuditEventEntity;

/** 审计事件表 MyBatis-Plus Mapper。 */
@Mapper
public interface AuditEventMapper extends BaseMapper<AuditEventEntity> {
}
