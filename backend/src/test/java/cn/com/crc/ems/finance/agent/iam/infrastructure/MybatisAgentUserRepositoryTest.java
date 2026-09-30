package cn.com.crc.ems.finance.agent.iam.infrastructure;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.iam.infrastructure.mapper.AgentUserMapper;

/**
 *
 * @Description: 验证 IAM 仓储将领域 UUID 转换为 MyBatis-Plus 字符串主键参数。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-30 00:20:00
 */
class MybatisAgentUserRepositoryTest {

    @Test
    void findByIdUsesStorageUuidTextInsteadOfDomainUuidObject() {
        AgentUserMapper mapper = org.mockito.Mockito.mock(AgentUserMapper.class);
        UUID userId = UUID.randomUUID();
        when(mapper.selectById(userId.toString())).thenReturn(null);

        new MybatisAgentUserRepository(mapper).findById(userId);

        verify(mapper).selectById(userId.toString());
    }
}
