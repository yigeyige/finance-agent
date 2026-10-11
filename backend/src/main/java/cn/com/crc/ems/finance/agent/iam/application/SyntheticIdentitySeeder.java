package cn.com.crc.ems.finance.agent.iam.application;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.governance.application.RuntimeBundleRepository;
import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundle;
import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundleStatus;
import cn.com.crc.ems.finance.agent.iam.domain.AgentUser;
import cn.com.crc.ems.finance.agent.iam.domain.AgentUserStatus;
import cn.com.crc.ems.finance.agent.iam.domain.RoleBinding;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/**
 *
 * @Description: 提供仅供 local/test 显式启用的确定性合成身份和运行版本。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Service
public class SyntheticIdentitySeeder {

    static final UUID OPERATOR_ID = UUID.fromString("00000000-0000-4000-8000-000000000001");
    static final UUID VIEWER_ID = UUID.fromString("00000000-0000-4000-8000-000000000002");
    static final UUID ADMIN_ID = UUID.fromString("00000000-0000-4000-8000-000000000003");
    static final UUID BUNDLE_ID = UUID.fromString("00000000-0000-4000-8000-000000000010");

    private final AgentUserRepository agentUserRepository;
    private final RoleBindingRepository roleBindingRepository;
    private final RuntimeBundleRepository runtimeBundleRepository;

    public SyntheticIdentitySeeder(AgentUserRepository agentUserRepository,
            RoleBindingRepository roleBindingRepository, RuntimeBundleRepository runtimeBundleRepository) {
        this.agentUserRepository = agentUserRepository;
        this.roleBindingRepository = roleBindingRepository;
        this.runtimeBundleRepository = runtimeBundleRepository;
    }

    /** 写入幂等的本地测试身份、角色绑定和已发布运行版本。 */
    public void seed() {
        Instant now = Instant.now();
        agentUserRepository.insertIfAbsent(user(OPERATOR_ID, "local-operator", "LOCAL-OPERATOR", "本地操作员", now));
        agentUserRepository.insertIfAbsent(user(VIEWER_ID, "local-viewer", "LOCAL-VIEWER", "本地查看员", now));
        agentUserRepository.insertIfAbsent(user(ADMIN_ID, "local-admin", "LOCAL-ADMIN", "本地平台管理员", now));
        roleBindingRepository.grant(binding(OPERATOR_ID, RoleCode.OPERATOR, OPERATOR_ID, now));
        roleBindingRepository.grant(binding(VIEWER_ID, RoleCode.VIEWER, OPERATOR_ID, now));
        roleBindingRepository.grant(binding(ADMIN_ID, RoleCode.PLATFORM_ADMIN, ADMIN_ID, now));
        runtimeBundleRepository.insertIfAbsent(new RuntimeBundle(BUNDLE_ID, "local", "2.0.3", "local-model",
                "local-prompt", "local-rules", "local-tools", "local-sources", null,
                "0000000000000000000000000000000000000000000000000000000000000001",
                RuntimeBundleStatus.PUBLISHED, now, OPERATOR_ID, now, OPERATOR_ID));
    }

    private static AgentUser user(UUID id, String subject, String employeeNo, String displayName, Instant now) {
        return new AgentUser(id, subject, employeeNo, displayName, AgentUserStatus.ACTIVE, now, null, now, null);
    }

    private static RoleBinding binding(UUID userId, RoleCode roleCode, UUID actorId, Instant now) {
        return new RoleBinding(userId, roleCode, actorId, now, null, actorId, now, actorId, now);
    }
}
