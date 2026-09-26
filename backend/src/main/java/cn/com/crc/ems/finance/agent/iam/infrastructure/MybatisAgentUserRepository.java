package cn.com.crc.ems.finance.agent.iam.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.iam.application.AgentUserRepository;
import cn.com.crc.ems.finance.agent.iam.domain.AgentUser;
import cn.com.crc.ems.finance.agent.iam.infrastructure.entity.AgentUserEntity;
import cn.com.crc.ems.finance.agent.iam.infrastructure.mapper.AgentUserMapper;

/** 使用 MyBatis-Plus Mapper 访问 IAM 身份账户。 */
@Repository
public class MybatisAgentUserRepository implements AgentUserRepository {

    private final AgentUserMapper mapper;

    public MybatisAgentUserRepository(AgentUserMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<AgentUser> findById(UUID userId) {
        return Optional.ofNullable(mapper.selectById(userId)).map(MybatisIamMappings::toDomain);
    }

    @Override
    public Optional<AgentUser> findByAuthSubject(String authSubject) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<AgentUserEntity>().eq("auth_subject", authSubject)))
                .map(MybatisIamMappings::toDomain);
    }

    @Override
    public Optional<AgentUser> findByEmployeeNo(String employeeNo) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<AgentUserEntity>().eq("employee_no", employeeNo)))
                .map(MybatisIamMappings::toDomain);
    }

    @Override
    public void save(AgentUser user) {
        mapper.updateById(MybatisIamMappings.toEntity(user));
    }

    @Override
    public void insertIfAbsent(AgentUser user) {
        if (mapper.selectById(user.userId()) == null) {
            mapper.insert(MybatisIamMappings.toEntity(user));
        }
    }
}
