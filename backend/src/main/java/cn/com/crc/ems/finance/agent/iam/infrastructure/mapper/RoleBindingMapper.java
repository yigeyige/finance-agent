package cn.com.crc.ems.finance.agent.iam.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.iam.infrastructure.entity.RoleBindingEntity;

/** IAM 角色授权表的 MyBatis-Plus Mapper。 */
@Mapper
public interface RoleBindingMapper extends BaseMapper<RoleBindingEntity> {
}
