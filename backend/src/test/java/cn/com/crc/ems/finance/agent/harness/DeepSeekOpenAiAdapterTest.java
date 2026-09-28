package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.agentscope.core.formatter.ResponseFormat;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.ThinkingBlock;
import io.agentscope.core.model.ChatResponse;
import io.agentscope.core.model.GenerateOptions;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;
import cn.com.crc.ems.finance.agent.harness.infrastructure.DeepSeekOpenAiAdapter;

class DeepSeekOpenAiAdapterTest {

    private HttpServer server;
    private AtomicReference<String> requestBody;
    private AtomicReference<String> authorization;
    private AtomicReference<Integer> status;

    @BeforeEach
    void setUp() throws IOException {
        requestBody = new AtomicReference<>();
        authorization = new AtomicReference<>();
        status = new AtomicReference<>(200);
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/chat/completions", exchange -> {
            requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            byte[] response = responseBody().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(responseStatus(), response.length);
            try (var output = exchange.getResponseBody()) {
                output.write(response);
            }
        });
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void sendsChatCompletionsRequestAndReturnsUsageMetadata() {
        FinanceAgentProperties.DeepSeek settings = settings("secret-key");
        DeepSeekOpenAiAdapter adapter = new DeepSeekOpenAiAdapter(settings);
        GenerateOptions options = adapter.options(Duration.ofSeconds(2), ResponseFormat.jsonObject(), null, 128);

        ChatResponse response = adapter.stream(List.of(Msg.builder().role(MsgRole.USER)
                .textContent("hello").build()), List.of(), options).blockLast(Duration.ofSeconds(5));

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("stub-response");
        assertThat(response.getUsage().getTotalTokens()).isEqualTo(5);
        assertThat(response.getFinishReason()).isEqualTo("stop");
        assertThat(response.getContent()).noneMatch(block -> block instanceof ThinkingBlock);
        assertThat(requestBody.get()).contains("\"model\":\"deepseek-flash\"");
        assertThat(requestBody.get()).contains("\"stream\":false");
        assertThat(requestBody.get()).contains("\"reasoning_effort\":\"high\"");
        assertThat(requestBody.get()).contains("\"thinking\":{\"type\":\"enabled\"}");
        assertThat(authorization.get()).isEqualTo("Bearer secret-key");
        assertThat(requestBody.get()).doesNotContain("secret-key");
    }

    @Test
    void forwardsThinkingOnlyToTheNextProviderRequest() {
        FinanceAgentProperties.DeepSeek settings = settings("secret-key");
        DeepSeekOpenAiAdapter adapter = new DeepSeekOpenAiAdapter(settings);
        Msg assistant = Msg.builder().role(MsgRole.ASSISTANT)
                .content(List.of(ThinkingBlock.builder().thinking("loop-reasoning-marker").build()))
                .build();

        adapter.stream(List.of(assistant), List.of(), adapter.options(Duration.ofSeconds(2), null, null, null))
                .blockLast(Duration.ofSeconds(5));

        assertThat(requestBody.get()).contains("loop-reasoning-marker");
    }

    @Test
    void defaultsUseDeepSeekFlashAndMissingKeyFailsWithoutSecretDisclosure() {
        FinanceAgentProperties.DeepSeek defaults = new FinanceAgentProperties.DeepSeek();
        assertThat(defaults.getBaseUrl()).isNull();
        assertThat(defaults.getModel()).isNull();

        IllegalStateException exception = null;
        try {
            new DeepSeekOpenAiAdapter(defaults);
        } catch (IllegalStateException expected) {
            exception = expected;
        }
        assertThat(exception).isNotNull();
        assertThat(exception).hasMessage("DeepSeek API key is required for live model calls");

        defaults.setApiKey("secret-key");
        DeepSeekOpenAiAdapter adapter = new DeepSeekOpenAiAdapter(defaults);
        assertThat(adapter.baseUrl()).isEqualTo(FinanceAgentProperties.DeepSeek.DEFAULT_BASE_URL);
        assertThat(adapter.modelName()).isEqualTo(FinanceAgentProperties.DeepSeek.DEFAULT_MODEL);
    }

    @Test
    void providerErrorPropagatesWithoutFallback() {
        FinanceAgentProperties.DeepSeek settings = settings("secret-key");
        DeepSeekOpenAiAdapter adapter = new DeepSeekOpenAiAdapter(settings);
        status.set(429);

        assertThatThrownBy(() -> adapter.stream(List.of(Msg.builder().role(MsgRole.USER)
                .textContent("hello").build()), List.of(), adapter.options(Duration.ofSeconds(2), null, null, null))
                .blockLast(Duration.ofSeconds(5)))
                .hasMessageContaining("429");
    }

    private FinanceAgentProperties.DeepSeek settings(String key) {
        FinanceAgentProperties.DeepSeek settings = new FinanceAgentProperties.DeepSeek();
        settings.setApiKey(key);
        settings.setBaseUrl("http://localhost:" + server.getAddress().getPort());
        settings.setModel("deepseek-flash");
        return settings;
    }

    private int responseStatus() {
        return status.get();
    }

    private String responseBody() {
        if (status.get() == 429) {
            return "{\"error\":{\"message\":\"rate limited\",\"type\":\"rate_limit_error\"}}";
        }
        return "{\"id\":\"stub-response\",\"object\":\"chat.completion\",\"created\":1,"
                + "\"model\":\"deepseek-flash\",\"choices\":[{\"index\":0,"
                + "\"message\":{\"role\":\"assistant\",\"content\":\"ok\","
                + "\"reasoning_content\":\"response-reasoning-marker\"},"
                + "\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":3,"
                + "\"completion_tokens\":2,\"total_tokens\":5}}";
    }
}
