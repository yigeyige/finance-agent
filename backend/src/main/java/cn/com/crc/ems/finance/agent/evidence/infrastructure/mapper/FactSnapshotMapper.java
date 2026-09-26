package cn.com.crc.ems.finance.agent.evidence.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.evidence.infrastructure.entity.FactSnapshotEntity;

/** 事实快照表的 MyBatis-Plus Mapper。 */
@Mapper
public interface FactSnapshotMapper extends BaseMapper<FactSnapshotEntity> {
}
