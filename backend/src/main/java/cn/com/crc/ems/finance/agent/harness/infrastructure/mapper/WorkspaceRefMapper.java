package cn.com.crc.ems.finance.agent.harness.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.harness.infrastructure.entity.WorkspaceRefEntity;

/**
 *
 * @Description: Harness workspace 引用的 MyBatis-Plus Mapper。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Mapper
public interface WorkspaceRefMapper extends BaseMapper<WorkspaceRefEntity> {
}
