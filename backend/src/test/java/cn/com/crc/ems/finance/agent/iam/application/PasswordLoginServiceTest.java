package cn.com.crc.ems.finance.agent.iam.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.iam.domain.AgentUser;
import cn.com.crc.ems.finance.agent.iam.domain.AgentUserStatus;
import cn.com.crc.ems.finance.agent.iam.domain.RoleBinding;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.iam.infrastructure.entity.UserPasswordEntity;
import cn.com.crc.ems.finance.agent.iam.infrastructure.mapper.UserPasswordMapper;

/**
 *
 * @Description: 验证本地账号密码登录的凭据、角色及会话失效边界。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-10 00:00:00
 */
class PasswordLoginServiceTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-4000-8000-000000000004");
    private static final Instant NOW = Instant.parse("2026-10-10T00:00:00Z");

    private AgentUserRepository users;
    private RoleBindingRepository roles;
    private UserPasswordMapper passwords;
    private PasswordLoginService service;

    /**
     *
     * @description: 创建隔离的身份、角色和凭据替身。
     *
     * @author: yigeyige99
     */
    @BeforeEach
    void setUp() {
        users = mock(AgentUserRepository.class);
        roles = mock(RoleBindingRepository.class);
        passwords = mock(UserPasswordMapper.class);
        service = new PasswordLoginService(users, roles, passwords);
    }

    /**
     *
     * @description: 正确密码和活动管理员角色建立会话，退出后会话不可继续使用。
     *
     * @author: yigeyige99
     */
    @Test
    void establishesAndClearsAnAuthenticatedSession() throws Exception {
        when(users.findByAuthSubject("admin")).thenReturn(Optional.of(activeUser()));
        when(roles.findByUserId(USER_ID)).thenReturn(List.of(adminBinding()));
        when(passwords.selectById(USER_ID.toString())).thenReturn(credential("correct-password"));

        Optional<PasswordLoginService.Login> login = service.login("admin", "correct-password");

        assertThat(login).isPresent();
        assertThat(login.orElseThrow().identity().hasRole(RoleCode.PLATFORM_ADMIN)).isTrue();
        assertThat(service.resolve(login.orElseThrow().token())).isPresent();

        service.logout(login.orElseThrow().token());

        assertThat(service.resolve(login.orElseThrow().token())).isEmpty();
    }

    /**
     *
     * @description: 错误密码或无活动角色均不能建立会话。
     *
     * @author: yigeyige99
     */
    @Test
    void rejectsInvalidCredentialsAndUsersWithoutActiveRoles() throws Exception {
        when(users.findByAuthSubject("admin")).thenReturn(Optional.of(activeUser()));
        when(passwords.selectById(USER_ID.toString())).thenReturn(credential("correct-password"));
        when(roles.findByUserId(USER_ID)).thenReturn(List.of());

        assertThat(service.login("admin", "wrong-password")).isEmpty();
        assertThat(service.login("admin", "correct-password")).isEmpty();
    }

    private static AgentUser activeUser() {
        return new AgentUser(USER_ID, "admin", "ADMIN", "平台管理员", AgentUserStatus.ACTIVE, NOW, null, NOW, null);
    }

    private static RoleBinding adminBinding() {
        return new RoleBinding(USER_ID, RoleCode.PLATFORM_ADMIN, USER_ID, NOW, null, USER_ID, NOW, USER_ID, NOW);
    }

    private static UserPasswordEntity credential(String plainPassword) throws Exception {
        UserPasswordEntity entity = new UserPasswordEntity();
        entity.userId = USER_ID.toString();
        entity.passwordSalt = "test-login-salt".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        entity.iterations = 100000;
        entity.algorithm = "PBKDF2WithHmacSHA256";
        PBEKeySpec spec = new PBEKeySpec(plainPassword.toCharArray(), entity.passwordSalt, entity.iterations, 256);
        try {
            entity.passwordHash = SecretKeyFactory.getInstance(entity.algorithm).generateSecret(spec).getEncoded();
            return entity;
        } finally {
            spec.clearPassword();
        }
    }
}
