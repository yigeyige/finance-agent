package cn.com.crc.ems.finance.agent.harness.infrastructure;

import java.util.List;
import java.util.Map;

import reactor.core.publisher.Flux;

import io.agentscope.core.message.ContentBlock;
import io.agentscope.core.message.TextBlock;
import io.agentscope.core.model.ChatResponse;
import io.agentscope.core.model.ChatUsage;
import io.agentscope.core.model.GenerateOptions;
import io.agentscope.core.model.Model;
import io.agentscope.core.model.ToolSchema;
import io.agentscope.core.message.Msg;

/** AgentScope Harness 冒烟使用的本地模型，不创建 HTTP 客户端或访问网络。 */
public final class StubAgentScopeModel implements Model {

    private final String modelName;

    public StubAgentScopeModel() {
        this("agentscope-local-stub-v1");
    }

    public StubAgentScopeModel(String modelName) {
        if (modelName == null || modelName.isBlank()) {
            throw new IllegalArgumentException("modelName must not be blank");
        }
        this.modelName = modelName;
    }

    @Override
    public Flux<ChatResponse> stream(List<Msg> messages, List<ToolSchema> tools, GenerateOptions options) {
        return Flux.just(new ChatResponse("stub-response", List.<ContentBlock>of(TextBlock.builder().text("stub-ok").build()),
                new ChatUsage(1, 1, 0.0), Map.of("source", "deterministic-stub"), "stop"));
    }

    @Override
    public String getModelName() {
        return modelName;
    }
}
