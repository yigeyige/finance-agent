package cn.com.crc.ems.finance.agent.iam.application;

import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Service;
import cn.com.crc.ems.finance.agent.iam.domain.AgentUserStatus;
import cn.com.crc.ems.finance.agent.iam.domain.RoleBinding;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.iam.infrastructure.entity.UserPasswordEntity;
import cn.com.crc.ems.finance.agent.iam.infrastructure.mapper.UserPasswordMapper;

/**
 *
 * @Description: 负责账号密码校验及内存会话生命周期，不记录敏感凭据。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-10 00:00:00
 */
@Service
public final class PasswordLoginService {

    public static final String COOKIE_NAME = "FINANCE_AGENT_SESSION";

    private static final String PASSWORD_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final long SESSION_TTL_SECONDS = 8 * 60 * 60L;

    private final AgentUserRepository users;
    private final RoleBindingRepository roles;
    private final UserPasswordMapper passwords;
    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    public PasswordLoginService(AgentUserRepository users, RoleBindingRepository roles, UserPasswordMapper passwords) {
        this.users = users;
        this.roles = roles;
        this.passwords = passwords;
    }

    /**
     *
     * @description: 校验活动账号的密码和活动角色，并创建随机不透明会话。
     *
     * @param: username 登录账号
     * @param: password 登录密码
     * @return: 验证成功后的会话及身份；失败时为空
     * @author: yigeyige99
     */
    public Optional<Login> login(String username, String password) {
        return users.findByAuthSubject(username)
                .filter(user -> user.status() == AgentUserStatus.ACTIVE)
                .filter(user -> verify(user.userId(), password))
                .map(this::identity)
                .flatMap(identity -> identity.map(this::createLogin));
    }

    /**
     *
     * @description: 按随机会话令牌解析尚未过期的可信身份。
     *
     * @param: token 浏览器携带的会话令牌
     * @return: 会话身份；令牌缺失、未知或过期时为空
     * @author: yigeyige99
     */
    public Optional<CurrentIdentity> resolve(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        Session session = sessions.get(token);
        if (session == null || !session.expiresAt().isAfter(Instant.now())) {
            sessions.remove(token);
            return Optional.empty();
        }
        return Optional.of(session.identity());
    }

    /**
     *
     * @description: 删除指定会话，令后续请求按未登录处理。
     *
     * @param: token 待失效的会话令牌
     * @author: yigeyige99
     */
    public void logout(String token) {
        if (token != null) {
            sessions.remove(token);
        }
    }

    private Login createLogin(CurrentIdentity identity) {
        String token = UUID.randomUUID() + UUID.randomUUID().toString();
        sessions.put(token, new Session(identity, Instant.now().plusSeconds(SESSION_TTL_SECONDS)));
        return new Login(token, identity);
    }

    private boolean verify(UUID userId, String password) {
        UserPasswordEntity credential = passwords.selectById(userId.toString());
        if (credential == null || password == null || credential.passwordHash == null || credential.passwordSalt == null
                || credential.iterations < 100000 || !PASSWORD_ALGORITHM.equals(credential.algorithm)) {
            return false;
        }
        char[] passwordChars = password.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(passwordChars, credential.passwordSalt, credential.iterations,
                credential.passwordHash.length * Byte.SIZE);
        try {
            byte[] candidate = SecretKeyFactory.getInstance(PASSWORD_ALGORITHM).generateSecret(spec).getEncoded();
            return MessageDigest.isEqual(candidate, credential.passwordHash);
        } catch (Exception ignored) {
            return false;
        } finally {
            spec.clearPassword();
            java.util.Arrays.fill(passwordChars, '\0');
        }
    }

    private Optional<CurrentIdentity> identity(cn.com.crc.ems.finance.agent.iam.domain.AgentUser user) {
        Set<RoleCode> activeRoles = roles.findByUserId(user.userId()).stream()
                .filter(binding -> binding.revokedAt() == null)
                .map(RoleBinding::roleCode)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        if (activeRoles.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new CurrentIdentity(user.userId(), user.authSubject(), user.displayName(), activeRoles));
    }

    private record Session(CurrentIdentity identity, Instant expiresAt) {
    }

    public record Login(String token, CurrentIdentity identity) {
    }
}
