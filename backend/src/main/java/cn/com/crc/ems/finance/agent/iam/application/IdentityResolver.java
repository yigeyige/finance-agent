package cn.com.crc.ems.finance.agent.iam.application;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.iam.domain.AgentUser;
import cn.com.crc.ems.finance.agent.iam.domain.RoleBinding;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import jakarta.servlet.http.HttpServletRequest;

/** 从生产 Principal 或 local/test 白名单标识解析可信身份。 */
@Service
public class IdentityResolver {

    private static final Map<String, UUID> TEST_IDENTITIES = Map.of(
            "operator", SyntheticIdentitySeeder.OPERATOR_ID,
            "viewer", SyntheticIdentitySeeder.VIEWER_ID,
            "platform-admin", SyntheticIdentitySeeder.ADMIN_ID);

    private final AgentUserRepository agentUserRepository;
    private final RoleBindingRepository roleBindingRepository;
    private final Environment environment;

    public IdentityResolver(AgentUserRepository agentUserRepository, RoleBindingRepository roleBindingRepository,
            Environment environment) {
        this.agentUserRepository = agentUserRepository;
        this.roleBindingRepository = roleBindingRepository;
        this.environment = environment;
    }

    /** 解析请求身份；无可信来源时返回空。 */
    public CurrentIdentity resolve(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            return agentUserRepository.findByAuthSubject(principal.getName()).map(this::toIdentity).orElse(null);
        }
        if (environment.acceptsProfiles("prod")) {
            return null;
        }
        String testIdentity = request.getHeader("X-Test-Identity");
        UUID userId = TEST_IDENTITIES.get(testIdentity == null ? "" : testIdentity.trim().toLowerCase());
        return userId == null ? null : agentUserRepository.findById(userId).map(this::toIdentity).orElse(null);
    }

    private CurrentIdentity toIdentity(AgentUser user) {
        Set<RoleCode> roles = roleBindingRepository.findByUserId(user.userId()).stream()
                .filter(this::active).map(RoleBinding::roleCode).collect(java.util.stream.Collectors.toUnmodifiableSet());
        return new CurrentIdentity(user.userId(), user.authSubject(), user.displayName(), roles);
    }

    private boolean active(RoleBinding binding) {
        return binding.revokedAt() == null;
    }
}
