package cn.com.crc.ems.finance.agent.governance.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.governance.infrastructure.entity.AuditEventEntity;

/**
 *
 * @Description: 审计事件表 MyBatis-Plus Mapper。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Mapper
public interface AuditEventMapper extends BaseMapper<AuditEventEntity> {
}
