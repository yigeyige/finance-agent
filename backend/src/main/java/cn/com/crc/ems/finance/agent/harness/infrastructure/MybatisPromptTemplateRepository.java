package cn.com.crc.ems.finance.agent.harness.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.harness.application.PromptTemplate;
import cn.com.crc.ems.finance.agent.harness.application.PromptTemplateRepository;
import cn.com.crc.ems.finance.agent.harness.infrastructure.entity.PromptTemplateEntity;
import cn.com.crc.ems.finance.agent.harness.infrastructure.mapper.PromptTemplateMapper;

/**
 *
 * @Description: 使用 MyBatis-Plus 持久化提示词模板。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-23 18:00:00
 */
@Repository
public class MybatisPromptTemplateRepository implements PromptTemplateRepository {

    private final PromptTemplateMapper mapper;

    public MybatisPromptTemplateRepository(PromptTemplateMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<PromptTemplate> findByCode(String templateCode) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<PromptTemplateEntity>().eq("template_code",
                templateCode))).map(MybatisPromptTemplateRepository::toDomain);
    }

    private static PromptTemplate toDomain(PromptTemplateEntity entity) {
        return new PromptTemplate(entity.templateCode, entity.content);
    }
}
