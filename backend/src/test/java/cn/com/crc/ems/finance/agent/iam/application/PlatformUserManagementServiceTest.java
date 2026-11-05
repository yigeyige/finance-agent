package cn.com.crc.ems.finance.agent.iam.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.iam.domain.AgentUser;
import cn.com.crc.ems.finance.agent.iam.domain.AgentUserStatus;
import cn.com.crc.ems.finance.agent.iam.domain.RoleBinding;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/**
 * @Description: 验证平台用户管理的账号保护与角色写入边界。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-05 00:00:00
 */
class PlatformUserManagementServiceTest {
    private static final Instant NOW = Instant.parse("2026-11-05T00:00:00Z");
    private final AgentUserRepository users = mock(AgentUserRepository.class);
    private final RoleBindingRepository roles = mock(RoleBindingRepository.class);
    private final PasswordLoginService passwords = mock(PasswordLoginService.class);
    private final PlatformUserManagementService service = new PlatformUserManagementService(users, roles, passwords,
            Clock.fixed(NOW, ZoneOffset.UTC));
    private final CurrentIdentity admin = new CurrentIdentity(UUID.randomUUID(), "manager", "管理员", Set.of(RoleCode.PLATFORM_ADMIN));

    @Test
    void createsOrdinaryUserWithCredentialAndRole() {
        var created = service.create(admin, "worker", "W001", "业务人员", RoleCode.PLATFORM_USER, "initial-password");
        assertThat(created.user().authSubject()).isEqualTo("worker");
        assertThat(created.user().status()).isEqualTo(AgentUserStatus.ACTIVE);
        assertThat(created.role()).isEqualTo(RoleCode.PLATFORM_USER);
        verify(users).insertIfAbsent(created.user());
        verify(roles).grant(any(RoleBinding.class));
        verify(passwords).setPassword(created.user().userId(), "initial-password");
    }

    @Test
    void neverChangesFixedAdminAccount() {
        UUID targetId = UUID.randomUUID();
        when(users.findById(targetId)).thenReturn(Optional.of(new AgentUser(targetId, "admin", "ADMIN", "平台管理员",
                AgentUserStatus.ACTIVE, NOW, null, NOW, null)));
        assertThatThrownBy(() -> service.disable(admin, targetId)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(roles, passwords);
    }

    @Test
    void rejectsSelfDisableAndDemotion() {
        when(users.findById(admin.userId())).thenReturn(Optional.of(new AgentUser(admin.userId(), "manager", "M001", "管理员",
                AgentUserStatus.ACTIVE, NOW, null, NOW, null)));
        when(roles.findByUserId(admin.userId())).thenReturn(List.of(new RoleBinding(admin.userId(), RoleCode.PLATFORM_ADMIN,
                admin.userId(), NOW, null, admin.userId(), NOW, admin.userId(), NOW)));
        assertThatThrownBy(() -> service.disable(admin, admin.userId())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.update(admin, admin.userId(), "管理员", RoleCode.PLATFORM_USER))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void omitsTechnicalSeedWithoutLoginRoleFromManagementList() {
        UUID seedId = UUID.randomUUID();
        AgentUser seed = new AgentUser(seedId, "local-operator", "LOCAL-OPERATOR", "种子创建人",
                AgentUserStatus.ACTIVE, NOW, null, NOW, null);
        AgentUser manager = new AgentUser(admin.userId(), "manager", "M001", "管理员",
                AgentUserStatus.ACTIVE, NOW, null, NOW, null);
        when(users.findAll()).thenReturn(List.of(seed, manager));
        when(roles.findByUserId(admin.userId())).thenReturn(List.of(new RoleBinding(admin.userId(),
                RoleCode.PLATFORM_ADMIN, admin.userId(), NOW, null, admin.userId(), NOW, admin.userId(), NOW)));
        assertThat(service.list(admin)).hasSize(1);
    }

    @Test
    void disableAndEnablePreserveUserIdentity() {
        UUID targetId = UUID.randomUUID();
        AgentUser user = new AgentUser(targetId, "worker", "W001", "业务人员", AgentUserStatus.ACTIVE,
                NOW, admin.userId(), NOW, admin.userId());
        when(users.findById(targetId)).thenReturn(Optional.of(user));
        when(roles.findByUserId(targetId)).thenReturn(List.of(new RoleBinding(targetId, RoleCode.PLATFORM_USER,
                admin.userId(), NOW, null, admin.userId(), NOW, admin.userId(), NOW)));
        var disabled = service.disable(admin, targetId);
        assertThat(disabled.user().status()).isEqualTo(AgentUserStatus.DISABLED);
        assertThat(disabled.user().userId()).isEqualTo(targetId);
        when(users.findById(targetId)).thenReturn(Optional.of(disabled.user()));
        var enabled = service.enable(admin, targetId);
        assertThat(enabled.user().status()).isEqualTo(AgentUserStatus.ACTIVE);
        assertThat(enabled.user().userId()).isEqualTo(targetId);
        verify(users).save(disabled.user());
        verify(users).save(enabled.user());
    }

    @Test
    void ordinaryUserCannotReadOrCreateAccounts() {
        var ordinary = new CurrentIdentity(UUID.randomUUID(), "worker", "业务人员", Set.of(RoleCode.PLATFORM_USER));
        assertThatThrownBy(() -> service.list(ordinary)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.create(ordinary, "new", "N001", "新用户",
                RoleCode.PLATFORM_USER, "initial-password")).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(users, roles, passwords);
    }

    @Test
    void cannotRemoveLastActiveAdministrator() {
        UUID targetId = UUID.randomUUID();
        AgentUser target = new AgentUser(targetId, "manager2", "M002", "另一管理员",
                AgentUserStatus.ACTIVE, NOW, null, NOW, null);
        when(users.findById(targetId)).thenReturn(Optional.of(target));
        when(users.findAll()).thenReturn(List.of(target));
        when(roles.findByUserId(targetId)).thenReturn(List.of(new RoleBinding(targetId, RoleCode.PLATFORM_ADMIN,
                admin.userId(), NOW, null, admin.userId(), NOW, admin.userId(), NOW)));
        assertThatThrownBy(() -> service.disable(admin, targetId)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.update(admin, targetId, "另一管理员", RoleCode.PLATFORM_USER))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
