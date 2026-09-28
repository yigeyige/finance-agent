package cn.com.crc.ems.finance.agent.harness.infrastructure;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import reactor.core.publisher.Flux;

import io.agentscope.core.message.Msg;
import io.agentscope.core.model.ChatResponse;
import io.agentscope.core.model.ExecutionConfig;
import io.agentscope.core.model.GenerateOptions;
import io.agentscope.core.model.Model;
import io.agentscope.core.model.ToolChoice;
import io.agentscope.core.model.ToolSchema;
import io.agentscope.core.formatter.ResponseFormat;
import io.agentscope.extensions.model.openai.OpenAIChatModel;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;

/**
 * DeepSeek OpenAI Chat Completions 适配器；复用 AgentScope 的 HTTP、工具和结构化输出实现。
 * 不负责模型切换，也不记录 API Key 或模型原文。
 */
public final class DeepSeekOpenAiAdapter {

    public static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    private final Model model;
    private final String baseUrl;
    private final String modelName;

    public DeepSeekOpenAiAdapter(FinanceAgentProperties.DeepSeek settings) {
        Objects.requireNonNull(settings, "deepseek settings are required");
        String apiKey = requireSecret(settings.getApiKey());
        this.baseUrl = valueOrDefault(settings.getBaseUrl(), FinanceAgentProperties.DeepSeek.DEFAULT_BASE_URL);
        this.modelName = valueOrDefault(settings.getModel(), FinanceAgentProperties.DeepSeek.DEFAULT_MODEL);
        this.model = OpenAIChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .endpointPath(CHAT_COMPLETIONS_PATH)
                .modelName(modelName)
                .stream(false)
                .nativeStructuredOutput(true)
                .nativeStructuredOutputWithTools(true)
                .build();
    }

    /** 返回 AgentScope 模型，调用方通过 Flux 订阅取消请求。 */
    public Model model() {
        return model;
    }

    public String baseUrl() {
        return baseUrl;
    }

    public String modelName() {
        return modelName;
    }

    /**
     * 构造一次调用的受限选项。schema、工具和响应 metadata 由 AgentScope 负责协议映射。
     */
    public GenerateOptions options(Duration timeout, ResponseFormat responseFormat,
            ToolChoice toolChoice, Integer maxTokens) {
        if (timeout == null || timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("timeout must be positive");
        }
        if (maxTokens != null && maxTokens < 1) {
            throw new IllegalArgumentException("maxTokens must be positive");
        }
        GenerateOptions.Builder builder = GenerateOptions.builder()
                .modelName(modelName)
                .baseUrl(baseUrl)
                .endpointPath(CHAT_COMPLETIONS_PATH)
                .stream(false)
                .executionConfig(ExecutionConfig.builder().timeout(timeout).maxAttempts(1).build());
        if (responseFormat != null) {
            builder.responseFormat(responseFormat);
        }
        if (toolChoice != null) {
            builder.toolChoice(toolChoice);
        }
        if (maxTokens != null) {
            builder.maxTokens(maxTokens);
        }
        return builder.build();
    }

    /** 显式暴露工具调用入口，保持响应 ID、usage、finish reason 等 AgentScope metadata。 */
    public Flux<ChatResponse> stream(List<Msg> messages, List<ToolSchema> tools,
            GenerateOptions options) {
        Objects.requireNonNull(messages, "messages are required");
        Objects.requireNonNull(tools, "tools are required");
        Objects.requireNonNull(options, "options are required");
        return model.stream(List.copyOf(messages), List.copyOf(tools), options);
    }

    private static String requireSecret(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("DeepSeek API key is required for live model calls");
        }
        return apiKey;
    }

    private static String valueOrDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
