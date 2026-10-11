package cn.com.crc.ems.finance.agent.harness.infrastructure;

import java.nio.file.Path;
import java.util.Objects;

import org.springframework.stereotype.Component;

import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.state.AgentStateStore;
import io.agentscope.core.state.InMemoryAgentStateStore;
import io.agentscope.harness.agent.HarnessAgent;

import cn.com.crc.ems.finance.agent.harness.application.HarnessIdentity;

/**
 *
 * @Description: 创建本地 HarnessAgent 冒烟实例，显式隔离用户、session、workspace 和状态存储。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Component
public final class HarnessAgentFactory {

    private final AgentStateStore stateStore;

    public HarnessAgentFactory() {
        this(new InMemoryAgentStateStore());
    }

    public HarnessAgentFactory(AgentStateStore stateStore) {
        this.stateStore = Objects.requireNonNull(stateStore, "stateStore is required");
    }

    /** 使用本地 Stub 构建不访问网络的 HarnessAgent。 */
    public HarnessAgent create(HarnessIdentity identity, Path workspace) {
        Objects.requireNonNull(identity, "identity is required");
        Objects.requireNonNull(workspace, "workspace is required");
        return HarnessAgent.builder()
                .name("finance-harness-smoke")
                .agentId("finance-harness-smoke")
                .model(new StubAgentScopeModel())
                .workspace(workspace.toAbsolutePath().normalize())
                .stateStore(stateStore)
                .defaultSessionId(identity.sessionId())
                .disableFilesystemTools()
                .disableShellTool()
                .disableDynamicSkills()
                .disableDynamicSubagents()
                .disableTranscript()
                .build();
    }

    /** 返回与 HarnessAgent 一致的显式运行时身份，缺少任何 ID 都不能创建调用上下文。 */
    public RuntimeContext runtimeContext(HarnessIdentity identity) {
        Objects.requireNonNull(identity, "identity is required");
        return RuntimeContext.builder().userId(identity.userId()).sessionId(identity.sessionId()).build();
    }
}
