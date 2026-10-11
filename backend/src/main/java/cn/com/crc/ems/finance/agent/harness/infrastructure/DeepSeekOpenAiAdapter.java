package cn.com.crc.ems.finance.agent.harness.infrastructure;

import java.time.Duration;
import java.util.concurrent.CancellationException;
import java.util.Map;
import java.util.List;
import java.util.Objects;

import reactor.core.publisher.Flux;

import io.agentscope.core.message.Msg;
import io.agentscope.core.model.ChatResponse;
import io.agentscope.core.model.ExecutionConfig;
import io.agentscope.core.model.GenerateOptions;
import io.agentscope.core.model.Model;
import io.agentscope.core.model.ModelException;
import io.agentscope.core.model.ToolChoice;
import io.agentscope.core.model.ToolSchema;
import io.agentscope.core.formatter.ResponseFormat;
import io.agentscope.extensions.model.openai.OpenAIChatModel;
import io.agentscope.extensions.model.openai.formatter.DeepSeekFormatter;
import io.agentscope.extensions.model.openai.exception.OpenAIException;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;
import cn.com.crc.ems.finance.agent.harness.application.ThinkingContentFilter;

/**
 *
 * @Description: DeepSeek OpenAI Chat Completions 适配器；复用 AgentScope 的 HTTP、工具和结构化输出实现。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public final class DeepSeekOpenAiAdapter {

    public static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    private final Model model;
    private final String baseUrl;
    private final String modelName;
    private final String reasoningEffort;

    public DeepSeekOpenAiAdapter(FinanceAgentProperties.DeepSeek settings) {
        Objects.requireNonNull(settings, "deepseek settings are required");
        String apiKey = requireSecret(settings.getApiKey());
        this.baseUrl = valueOrDefault(settings.getBaseUrl(), FinanceAgentProperties.DeepSeek.DEFAULT_BASE_URL);
        this.modelName = valueOrDefault(settings.getModel(), FinanceAgentProperties.DeepSeek.DEFAULT_MODEL);
        this.reasoningEffort = "high";
        this.model = OpenAIChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .endpointPath(CHAT_COMPLETIONS_PATH)
                .modelName(modelName)
                .stream(false)
                .formatter(new DeepSeekFormatter())
                .nativeStructuredOutput(true)
                .nativeStructuredOutputWithTools(true)
                .build();
    }

    /** 使用已解密且仅驻留于本次调用内存的数据库模型配置创建客户端。 */
    public DeepSeekOpenAiAdapter(String baseUrl, String modelName, String reasoningEffort, String apiKey) {
        this.baseUrl = valueOrDefault(baseUrl, FinanceAgentProperties.DeepSeek.DEFAULT_BASE_URL);
        this.modelName = valueOrDefault(modelName, FinanceAgentProperties.DeepSeek.DEFAULT_MODEL);
        this.reasoningEffort = valueOrDefault(reasoningEffort, "high");
        this.model = OpenAIChatModel.builder().apiKey(requireSecret(apiKey)).baseUrl(this.baseUrl)
                .endpointPath(CHAT_COMPLETIONS_PATH).modelName(this.modelName).stream(false)
                .formatter(new DeepSeekFormatter()).nativeStructuredOutput(true).nativeStructuredOutputWithTools(true).build();
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
                .reasoningEffort(reasoningEffort)
                .additionalBodyParam("thinking", Map.of("type", "enabled"))
                .additionalBodyParam("include_reasoning", true)
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
        return model.stream(List.copyOf(messages), List.copyOf(tools), options)
                .map(ThinkingContentFilter::filterResponse)
                .onErrorMap(this::sanitizeFailure);
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

    private RuntimeException sanitizeFailure(Throwable failure) {
        if (failure instanceof CancellationException) {
            return (CancellationException) failure;
        }
        if (failure instanceof ModelException modelException
                && "DeepSeek model call failed".equals(modelException.getMessage())) {
            return modelException;
        }
        String message = "DeepSeek model call failed";
        OpenAIException openAiException = findOpenAiException(failure);
        if (openAiException != null && openAiException.getStatusCode() != null) {
            message += " with status " + openAiException.getStatusCode();
        }
        return new ModelException(message, modelName, "deepseek");
    }

    private static OpenAIException findOpenAiException(Throwable failure) {
        Throwable current = failure;
        while (current != null) {
            if (current instanceof OpenAIException openAiException) {
                return openAiException;
            }
            current = current.getCause();
        }
        return null;
    }
}
