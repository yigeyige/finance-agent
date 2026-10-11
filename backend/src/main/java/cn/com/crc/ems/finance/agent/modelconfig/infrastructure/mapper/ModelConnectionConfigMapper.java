package cn.com.crc.ems.finance.agent.modelconfig.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.com.crc.ems.finance.agent.modelconfig.infrastructure.entity.ModelConnectionConfigEntity;

/**
 *
 * @Description: 大模型连接配置的 MyBatis-Plus Mapper。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Mapper
public interface ModelConnectionConfigMapper extends BaseMapper<ModelConnectionConfigEntity> {
}
