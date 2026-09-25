package cn.com.crc.ems.finance.agent.bootstrap;

import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.permission.PermissionContextState;
import io.agentscope.core.state.AgentStateStore;
import io.agentscope.extensions.model.openai.OpenAIChatModel;
import io.agentscope.harness.agent.DistributedStore;
import io.agentscope.harness.agent.HarnessAgent;
import io.agentscope.harness.agent.workspace.WorkspaceManager;

/**
 * Compile-only probe for the locked AgentScope 2.0.3 API surface.
 *
 * <p>This class is deliberately not a runtime bootstrap. It keeps the
 * dependency decision executable without making a network call or requiring
 * a real model credential.</p>
 */
public final class AgentScopeApiProbe {

    private AgentScopeApiProbe() {
    }

    public static RuntimeContext runtimeContext() {
        return RuntimeContext.builder()
                .userId("stub-user")
                .sessionId("stub-session")
                .build();
    }

    public static OpenAIChatModel openAiCompatibleModel() {
        return OpenAIChatModel.builder()
                .apiKey(System.getenv("DEEPSEEK_API_KEY"))
                .modelName("deepseek-chat")
                .baseUrl("https://api.deepseek.com")
                .stream(false)
                .build();
    }

    public static HarnessAgent.Builder harnessBuilder() {
        return HarnessAgent.builder()
                .name("finance-agent-probe")
                .model(openAiCompatibleModel())
                .disableFilesystemTools()
                .disableShellTool();
    }

    public static Class<?> permissionSpi() {
        return PermissionContextState.class;
    }

    public static Class<?> workspaceSpi() {
        return WorkspaceManager.class;
    }

    public static Class<?> stateStoreSpi() {
        return AgentStateStore.class;
    }

    public static Class<?> distributedStoreSpi() {
        return DistributedStore.class;
    }
}
