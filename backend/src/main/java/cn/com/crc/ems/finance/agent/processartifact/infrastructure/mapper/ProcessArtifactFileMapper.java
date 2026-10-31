package cn.com.crc.ems.finance.agent.processartifact.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.processartifact.infrastructure.entity.ProcessArtifactFileEntity;

/** 批次文件表映射。 */
@Mapper
public interface ProcessArtifactFileMapper extends BaseMapper<ProcessArtifactFileEntity> { }
