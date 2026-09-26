package cn.com.crc.ems.finance.agent.iam.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.iam.infrastructure.entity.AgentUserEntity;

/** IAM 身份表的 MyBatis-Plus Mapper。 */
@Mapper
public interface AgentUserMapper extends BaseMapper<AgentUserEntity> {
}
