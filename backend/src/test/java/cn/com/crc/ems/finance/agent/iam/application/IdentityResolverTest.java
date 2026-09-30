package cn.com.crc.ems.finance.agent.iam.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;

import cn.com.crc.ems.finance.agent.iam.domain.AgentUser;
import cn.com.crc.ems.finance.agent.iam.domain.AgentUserStatus;
import cn.com.crc.ems.finance.agent.iam.domain.RoleBinding;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/**
 *
 * @Description: 验证 P1 测试认证适配器的白名单、生产隔离和撤销角色失败关闭边界。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-30 23:55:00
 */
class IdentityResolverTest {

    private static final Instant NOW = Instant.parse("2026-09-30T00:00:00Z");

    private AgentUserRepository agentUserRepository;
    private RoleBindingRepository roleBindingRepository;
    private Environment environment;
    private IdentityResolver resolver;

    /**
     *
     * @description: 初始化隔离的身份仓储和环境替身。
     *
     * @author: yigeyige99
     */
    @BeforeEach
    void setUp() {
        agentUserRepository = mock(AgentUserRepository.class);
        roleBindingRepository = mock(RoleBindingRepository.class);
        environment = mock(Environment.class);
        resolver = new IdentityResolver(agentUserRepository, roleBindingRepository, environment);
    }

    /**
     *
     * @description: local/test 白名单身份解析为具有对应活动角色的当前身份。
     *
     * @author: yigeyige99
     */
    @Test
    void resolvesWhitelistedLocalTestIdentity() {
        localOrTestProfile();
        AgentUser user = user();
        when(agentUserRepository.findById(SyntheticIdentitySeeder.OPERATOR_ID)).thenReturn(Optional.of(user));
        when(roleBindingRepository.findByUserId(SyntheticIdentitySeeder.OPERATOR_ID))
                .thenReturn(List.of(binding(RoleCode.OPERATOR, null)));

        CurrentIdentity identity = resolver.resolve(testRequest("operator"));

        assertThat(identity).isNotNull();
        assertThat(identity.userId()).isEqualTo(SyntheticIdentitySeeder.OPERATOR_ID);
        assertThat(identity.hasRole(RoleCode.OPERATOR)).isTrue();
    }

    /**
     *
     * @description: 未登记测试身份不创建可信上下文。
     *
     * @author: yigeyige99
     */
    @Test
    void rejectsUnknownTestIdentity() {
        localOrTestProfile();

        assertThat(resolver.resolve(testRequest("unknown"))).isNull();

        verifyNoInteractions(agentUserRepository, roleBindingRepository);
    }

    /**
     *
     * @description: prod profile 忽略测试身份入口，不查询合成用户或角色。
     *
     * @author: yigeyige99
     */
    @Test
    void rejectsTestIdentityInProduction() {
        when(environment.acceptsProfiles("prod")).thenReturn(true);

        assertThat(resolver.resolve(testRequest("operator"))).isNull();

        verifyNoInteractions(agentUserRepository, roleBindingRepository);
    }

    /**
     *
     * @description: 全部角色已撤销的用户不构造空角色身份，并按无可信身份处理。
     *
     * @author: yigeyige99
     */
    @Test
    void rejectsIdentityWhoseRolesAreAllRevoked() {
        localOrTestProfile();
        when(agentUserRepository.findById(SyntheticIdentitySeeder.OPERATOR_ID)).thenReturn(Optional.of(user()));
        when(roleBindingRepository.findByUserId(SyntheticIdentitySeeder.OPERATOR_ID))
                .thenReturn(List.of(binding(RoleCode.OPERATOR, NOW)));

        assertThat(resolver.resolve(testRequest("operator"))).isNull();
    }

    private void localOrTestProfile() {
        when(environment.acceptsProfiles("prod")).thenReturn(false);
    }

    private static MockHttpServletRequest testRequest(String identity) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Test-Identity", identity);
        return request;
    }

    private static AgentUser user() {
        return new AgentUser(SyntheticIdentitySeeder.OPERATOR_ID, "local-operator", "LOCAL-OPERATOR", "本地操作员",
                AgentUserStatus.ACTIVE, NOW, null, NOW, null);
    }

    private static RoleBinding binding(RoleCode roleCode, Instant revokedAt) {
        return new RoleBinding(SyntheticIdentitySeeder.OPERATOR_ID, roleCode, SyntheticIdentitySeeder.OPERATOR_ID, NOW,
                revokedAt, SyntheticIdentitySeeder.OPERATOR_ID, NOW, SyntheticIdentitySeeder.OPERATOR_ID, NOW);
    }
}
