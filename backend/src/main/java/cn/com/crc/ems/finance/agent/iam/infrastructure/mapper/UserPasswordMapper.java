package cn.com.crc.ems.finance.agent.iam.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.com.crc.ems.finance.agent.iam.infrastructure.entity.UserPasswordEntity;

/**
 *
 * @Description: IAM 用户密码凭据访问 Mapper。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Mapper
public interface UserPasswordMapper extends BaseMapper<UserPasswordEntity> {
}
