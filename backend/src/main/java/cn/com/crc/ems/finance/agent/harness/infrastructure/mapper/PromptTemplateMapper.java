package cn.com.crc.ems.finance.agent.harness.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.harness.infrastructure.entity.PromptTemplateEntity;

/**
 *
 * @Description: 提示词模板的 MyBatis-Plus Mapper。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-23 18:00:00
 */
@Mapper
public interface PromptTemplateMapper extends BaseMapper<PromptTemplateEntity> {
}
