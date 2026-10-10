package cn.com.crc.ems.finance.agent.iam.application;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.iam.domain.AgentUser;
import cn.com.crc.ems.finance.agent.iam.domain.RoleBinding;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @Description: 解析会话、生产 Principal 或 local/test 白名单标识，并只提供具有活动角色的可信身份。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-30 23:55:00
 */
@Service
public class IdentityResolver {

    private static final Map<String, UUID> TEST_IDENTITIES = Map.of(
            "operator", SyntheticIdentitySeeder.OPERATOR_ID,
            "viewer", SyntheticIdentitySeeder.VIEWER_ID,
            "platform-admin", SyntheticIdentitySeeder.ADMIN_ID);

    private final AgentUserRepository agentUserRepository;
    private final RoleBindingRepository roleBindingRepository;
    private final Environment environment;
    private final PasswordLoginService passwordLoginService;

    public IdentityResolver(AgentUserRepository agentUserRepository, RoleBindingRepository roleBindingRepository,
            Environment environment) {
        this(agentUserRepository, roleBindingRepository, environment, null);
    }

    @Autowired
    public IdentityResolver(AgentUserRepository agentUserRepository, RoleBindingRepository roleBindingRepository,
            Environment environment, PasswordLoginService passwordLoginService) {
        this.agentUserRepository = agentUserRepository;
        this.roleBindingRepository = roleBindingRepository;
        this.environment = environment;
        this.passwordLoginService = passwordLoginService;
    }

    /**
     *
     * @description: 按会话、生产 Principal、local/test 白名单的顺序解析请求身份；无可信来源时返回空。
     *
     * @param: request 当前 HTTP 请求
     * @return: 当前可信身份；无法解析时为 null
     * @author: yigeyige99
     */
    public CurrentIdentity resolve(HttpServletRequest request) {
        if (passwordLoginService != null && request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if (PasswordLoginService.COOKIE_NAME.equals(cookie.getName())) {
                    var identity = passwordLoginService.resolve(cookie.getValue());
                    if (identity.isPresent()) return identity.get();
                }
            }
        }
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            return agentUserRepository.findByAuthSubject(principal.getName()).map(this::toIdentity).orElse(null);
        }
        if (environment.acceptsProfiles("prod")) {
            return null;
        }
        String testIdentity = request.getHeader("X-Test-Identity");
        if (testIdentity == null) {
            testIdentity = request.getParameter("testIdentity");
        }
        UUID userId = TEST_IDENTITIES.get(testIdentity == null ? "" : testIdentity.trim().toLowerCase());
        return userId == null ? null : agentUserRepository.findById(userId).map(this::toIdentity).orElse(null);
    }

    /**
     *
     * @description: 将持久化用户及其未撤销角色转换为当前可信身份；没有活动角色时失败关闭。
     *
     * @param: user 已解析的持久化用户
     * @return: 具有至少一个活动角色的当前身份；无活动角色时返回 null
     * @author: yigeyige99
     */
    private CurrentIdentity toIdentity(AgentUser user) {
        Set<RoleCode> roles = roleBindingRepository.findByUserId(user.userId()).stream()
                .filter(this::active).map(RoleBinding::roleCode).collect(java.util.stream.Collectors.toUnmodifiableSet());
        if (roles.isEmpty()) {
            return null;
        }
        return new CurrentIdentity(user.userId(), user.authSubject(), user.displayName(), roles);
    }

    private boolean active(RoleBinding binding) {
        return binding.revokedAt() == null;
    }
}
