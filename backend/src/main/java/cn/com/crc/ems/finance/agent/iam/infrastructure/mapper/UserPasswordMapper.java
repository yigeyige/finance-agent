package cn.com.crc.ems.finance.agent.iam.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.com.crc.ems.finance.agent.iam.infrastructure.entity.UserPasswordEntity;

/** IAM 用户密码凭据访问 Mapper。 */
@Mapper
public interface UserPasswordMapper extends BaseMapper<UserPasswordEntity> {
}
