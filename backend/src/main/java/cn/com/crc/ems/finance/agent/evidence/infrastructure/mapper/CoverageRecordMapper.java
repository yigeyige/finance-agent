package cn.com.crc.ems.finance.agent.evidence.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.evidence.infrastructure.entity.CoverageRecordEntity;

/** 覆盖记录表的 MyBatis-Plus Mapper。 */
@Mapper
public interface CoverageRecordMapper extends BaseMapper<CoverageRecordEntity> {
}
