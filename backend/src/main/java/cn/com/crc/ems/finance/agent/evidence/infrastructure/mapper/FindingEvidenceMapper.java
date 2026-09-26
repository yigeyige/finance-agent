package cn.com.crc.ems.finance.agent.evidence.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.evidence.infrastructure.entity.FindingEvidenceEntity;

/** 判断证据引用表的 MyBatis-Plus Mapper。 */
@Mapper
public interface FindingEvidenceMapper extends BaseMapper<FindingEvidenceEntity> {
}
