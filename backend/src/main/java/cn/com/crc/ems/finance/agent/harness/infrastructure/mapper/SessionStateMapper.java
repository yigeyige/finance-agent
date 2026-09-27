package cn.com.crc.ems.finance.agent.harness.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.harness.infrastructure.entity.SessionStateEntity;

/** Harness session 状态的 MyBatis-Plus Mapper。 */
@Mapper
public interface SessionStateMapper extends BaseMapper<SessionStateEntity> {
}
