package cn.com.crc.ems.finance.agent.iam.application;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.iam.domain.AgentUser;
import cn.com.crc.ems.finance.agent.iam.domain.AgentUserStatus;
import cn.com.crc.ems.finance.agent.iam.domain.RoleBinding;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;

/**
 * @Description: 平台管理员用户管理用例，保护固定账号并保持角色及密码事务一致。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-05 00:00:00
 */
@Service
public class PlatformUserManagementService {
    private final AgentUserRepository users;
    private final RoleBindingRepository roles;
    private final PasswordLoginService passwords;
    private final Clock clock;

    public PlatformUserManagementService(AgentUserRepository users, RoleBindingRepository roles,
            PasswordLoginService passwords, Clock clock) {
        this.users = users;
        this.roles = roles;
        this.passwords = passwords;
        this.clock = clock;
    }

    /** 查询所有用户及其当前角色，不包含密码信息。 */
    @Transactional(readOnly = true)
    public List<ManagedUser> list(CurrentIdentity actor) {
        requireAdmin(actor);
        return users.findAll().stream().map(user -> {
            RoleCode role = roleOfOrNull(user.userId());
            return role == null ? null : new ManagedUser(user, role);
        }).filter(java.util.Objects::nonNull).toList();
    }

    /** 创建唯一账号、单一角色和初始密码。 */
    @Transactional
    public ManagedUser create(CurrentIdentity actor, String authSubject, String employeeNo, String displayName,
            RoleCode role, String password) {
        requireAdmin(actor);
        String account = requireText(authSubject, 200, "登录账号");
        String employee = requireText(employeeNo, 100, "工号");
        String name = requireText(displayName, 200, "显示名称");
        requireRole(role);
        if (account.equals("admin") || users.findByAuthSubject(account).isPresent()
                || users.findByEmployeeNo(employee).isPresent()) throw new IllegalArgumentException("登录账号或工号已存在");
        Instant now = Instant.now(clock);
        AgentUser user = new AgentUser(UuidGenerator.newUuid(), account, employee, name, AgentUserStatus.ACTIVE,
                now, actor.userId(), now, actor.userId());
        users.insertIfAbsent(user);
        roles.grant(new RoleBinding(user.userId(), role, actor.userId(), now, null,
                actor.userId(), now, actor.userId(), now));
        passwords.setPassword(user.userId(), password);
        return new ManagedUser(user, role);
    }

    /** 修改显示名称及单一角色；自身管理员不得降权。 */
    @Transactional
    public ManagedUser update(CurrentIdentity actor, UUID userId, String displayName, RoleCode role) {
        AgentUser user = writable(actor, userId);
        String name = requireText(displayName, 200, "显示名称");
        requireRole(role);
        RoleCode oldRole = roleOf(userId);
        if (userId.equals(actor.userId()) && role != RoleCode.PLATFORM_ADMIN)
            throw new IllegalArgumentException("不能移除自己的管理员角色");
        if (oldRole == RoleCode.PLATFORM_ADMIN && role != oldRole) requireAnotherAdministrator(userId);
        Instant now = Instant.now(clock);
        AgentUser updated = new AgentUser(userId, user.authSubject(), user.employeeNo(), name, user.status(),
                user.createdAt(), user.createdBy(), now, actor.userId());
        users.save(updated);
        if (oldRole != role) {
            roles.revoke(userId, oldRole, now, actor.userId());
            roles.grant(new RoleBinding(userId, role, actor.userId(), now, null,
                    actor.userId(), now, actor.userId(), now));
        }
        return new ManagedUser(updated, role);
    }

    /** 停用账号并保留业务记录。 */
    @Transactional
    public ManagedUser disable(CurrentIdentity actor, UUID userId) {
        AgentUser user = writable(actor, userId);
        if (actor.userId().equals(userId)) throw new IllegalArgumentException("不能停用自己的账号");
        RoleCode role = roleOf(userId);
        if (role == RoleCode.PLATFORM_ADMIN) requireAnotherAdministrator(userId);
        return changeStatus(user, role, AgentUserStatus.DISABLED, actor.userId());
    }

    /** 重新启用保留的账号。 */
    @Transactional
    public ManagedUser enable(CurrentIdentity actor, UUID userId) {
        AgentUser user = writable(actor, userId);
        return changeStatus(user, roleOf(userId), AgentUserStatus.ACTIVE, actor.userId());
    }

    /** 重置他人或自己的密码，固定 admin 不在管理范围内。 */
    @Transactional
    public void resetPassword(CurrentIdentity actor, UUID userId, String password) {
        writable(actor, userId);
        passwords.setPassword(userId, password);
    }

    private ManagedUser changeStatus(AgentUser user, RoleCode role, AgentUserStatus status, UUID actorId) {
        AgentUser updated = new AgentUser(user.userId(), user.authSubject(), user.employeeNo(), user.displayName(),
                status, user.createdAt(), user.createdBy(), Instant.now(clock), actorId);
        users.save(updated);
        return new ManagedUser(updated, role);
    }

    private AgentUser writable(CurrentIdentity actor, UUID userId) {
        requireAdmin(actor);
        AgentUser user = users.findById(userId).orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if ("admin".equals(user.authSubject())) throw new IllegalArgumentException("固定管理员账号不可操作");
        return user;
    }

    private RoleCode roleOf(UUID userId) {
        RoleCode role = roleOfOrNull(userId);
        if (role == null) throw new IllegalStateException("用户缺少活动角色");
        return role;
    }

    private RoleCode roleOfOrNull(UUID userId) {
        List<RoleCode> active = roles.findByUserId(userId).stream().filter(binding -> binding.revokedAt() == null)
                .map(RoleBinding::roleCode).distinct().toList();
        if (active.size() > 1) throw new IllegalStateException("用户角色状态不唯一");
        if (active.isEmpty()) return null;
        return active.get(0);
    }

    private void requireAnotherAdministrator(UUID targetId) {
        long others = users.findAll().stream().filter(user -> user.status() == AgentUserStatus.ACTIVE)
                .filter(user -> !user.userId().equals(targetId))
                .filter(user -> roleOfOrNull(user.userId()) == RoleCode.PLATFORM_ADMIN).count();
        if (others == 0) throw new IllegalArgumentException("至少保留一位有效管理员");
    }

    private static void requireAdmin(CurrentIdentity actor) {
        if (actor == null || !actor.hasRole(RoleCode.PLATFORM_ADMIN))
            throw new IllegalArgumentException("当前身份无权管理用户");
    }

    private static void requireRole(RoleCode role) {
        if (role == null) throw new IllegalArgumentException("请选择角色");
    }

    private static String requireText(String value, int maxLength, String label) {
        if (value == null || value.isBlank() || value.trim().length() > maxLength)
            throw new IllegalArgumentException(label + "不合法");
        return value.trim();
    }

    public record ManagedUser(AgentUser user, RoleCode role) { }
}
