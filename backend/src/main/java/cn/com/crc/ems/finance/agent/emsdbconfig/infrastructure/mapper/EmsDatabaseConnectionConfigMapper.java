package cn.com.crc.ems.finance.agent.emsdbconfig.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.emsdbconfig.infrastructure.entity.EmsDatabaseConnectionConfigEntity;

/** EMS 数据库连接配置的 MyBatis-Plus Mapper。 */
@Mapper
public interface EmsDatabaseConnectionConfigMapper extends BaseMapper<EmsDatabaseConnectionConfigEntity> {
}
