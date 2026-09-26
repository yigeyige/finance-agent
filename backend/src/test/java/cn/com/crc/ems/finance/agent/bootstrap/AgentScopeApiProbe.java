package cn.com.crc.ems.finance.agent.bootstrap;

import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.permission.PermissionContextState;
import io.agentscope.core.state.AgentStateStore;
import io.agentscope.extensions.model.openai.OpenAIChatModel;
import io.agentscope.harness.agent.DistributedStore;
import io.agentscope.harness.agent.HarnessAgent;
import io.agentscope.harness.agent.workspace.WorkspaceManager;

/** Compile-only compatibility probe for the locked AgentScope API surface. */
final class AgentScopeApiProbe {

    private AgentScopeApiProbe() {
    }

    static RuntimeContext runtimeContext() {
        return RuntimeContext.builder().userId("test-user").sessionId("test-session").build();
    }

    static OpenAIChatModel openAiCompatibleModel() {
        return OpenAIChatModel.builder()
                .apiKey("test-api-key")
                .modelName("test-model")
                .baseUrl("https://example.invalid")
                .stream(false)
                .build();
    }

    static HarnessAgent.Builder harnessBuilder() {
        return HarnessAgent.builder().name("compatibility-probe").model(openAiCompatibleModel())
                .disableFilesystemTools().disableShellTool();
    }

    static Class<?> permissionSpi() {
        return PermissionContextState.class;
    }

    static Class<?> workspaceSpi() {
        return WorkspaceManager.class;
    }

    static Class<?> stateStoreSpi() {
        return AgentStateStore.class;
    }

    static Class<?> distributedStoreSpi() {
        return DistributedStore.class;
    }
}
