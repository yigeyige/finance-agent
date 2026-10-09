package cn.com.crc.ems.finance.agent.modelconfig.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.com.crc.ems.finance.agent.modelconfig.infrastructure.entity.ModelConnectionConfigEntity;

/** 大模型连接配置的 MyBatis-Plus Mapper。 */
@Mapper
public interface ModelConnectionConfigMapper extends BaseMapper<ModelConnectionConfigEntity> {
}
