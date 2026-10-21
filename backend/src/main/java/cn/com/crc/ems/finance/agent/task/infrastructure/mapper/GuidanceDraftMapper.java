package cn.com.crc.ems.finance.agent.task.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.task.infrastructure.entity.GuidanceDraftEntity;

/**
 *
 * @Description: 业务处理草稿表的 MyBatis-Plus Mapper。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-20 22:00:00
 */
@Mapper
public interface GuidanceDraftMapper extends BaseMapper<GuidanceDraftEntity> {
}
