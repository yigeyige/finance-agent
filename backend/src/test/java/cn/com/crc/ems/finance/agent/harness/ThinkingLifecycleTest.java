package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.junit.jupiter.api.Test;

import io.agentscope.core.message.ContentBlock;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.TextBlock;
import io.agentscope.core.message.ThinkingBlock;
import io.agentscope.core.message.ToolCallState;
import io.agentscope.core.message.ToolUseBlock;
import io.agentscope.core.model.ChatResponse;
import io.agentscope.core.model.ChatUsage;

import cn.com.crc.ems.finance.agent.harness.application.EphemeralThinkingContext;
import cn.com.crc.ems.finance.agent.harness.application.ThinkingContentFilter;
import cn.com.crc.ems.finance.agent.harness.infrastructure.DeepSeekOpenAiAdapter;
import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;

class ThinkingLifecycleTest {

    @Test
    void ephemeralContextIsClearedAfterClose() {
        EphemeralThinkingContext context = new EphemeralThinkingContext();
        context.capture("reasoning-marker");
        assertThat(context.reasoningContent()).contains("reasoning-marker");

        context.close();

        assertThat(context.isClosed()).isTrue();
        assertThat(context.reasoningContent()).isEmpty();
        assertThatThrownBy(() -> context.capture("late-marker"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void jsonAndChatResponseFiltersRemoveReasoningAtBoundary() {
        var value = JsonNodeFactory.instance.objectNode()
                .put("final", "ok")
                .put("reasoning_content", "reasoning-marker");
        value.set("nested", JsonNodeFactory.instance.objectNode().put("thinking", "nested-marker"));
        assertThat(ThinkingContentFilter.filter(value).toString()).doesNotContain("reasoning-marker", "nested-marker");

        ThinkingBlock thinking = ThinkingBlock.builder().thinking("reasoning-marker").build();
        ToolUseBlock tool = ToolUseBlock.builder().id("call-1").name("safe-tool")
                .input(Map.of("amount", BigDecimal.ONE))
                .metadata(Map.of("reasoning_content", "reasoning-marker", "safe", "kept"))
                .state(ToolCallState.PENDING).build();
        ChatResponse response = ChatResponse.builder().id("response-1")
                .content(List.of(thinking, TextBlock.builder().text("final-answer").build(), tool))
                .usage(new ChatUsage(2, 3, 0.0)).metadata(Map.of("thinking", "reasoning-marker"))
                .finishReason("stop").build();

        ChatResponse filtered = ThinkingContentFilter.filterResponse(response);

        assertThat(filtered.getContent()).hasSize(2)
                .noneMatch(block -> block instanceof ThinkingBlock)
                .anyMatch(block -> block instanceof TextBlock text && "final-answer".equals(text.getText()));
        ToolUseBlock filteredTool = (ToolUseBlock) filtered.getContent().get(1);
        assertThat(filteredTool.getMetadata()).containsEntry("safe", "kept")
                .doesNotContainKey("reasoning_content");
        assertThat(filtered.getMetadata()).doesNotContainKey("thinking");
    }

    @Test
    void adapterUsesDeepSeekFormatterAndThinkingHighByDefault() {
        FinanceAgentProperties.DeepSeek settings = new FinanceAgentProperties.DeepSeek();
        settings.setApiKey("not-logged");
        DeepSeekOpenAiAdapter adapter = new DeepSeekOpenAiAdapter(settings);

        var options = adapter.options(Duration.ofSeconds(3), null, null, 64);

        assertThat(options.getReasoningEffort()).isEqualTo("high");
        assertThat(options.getAdditionalBodyParams()).containsEntry("thinking", Map.of("type", "enabled"));
        assertThat(options.getAdditionalBodyParams()).containsEntry("include_reasoning", true);
        assertThat(options.getExecutionConfig().getMaxAttempts()).isEqualTo(1);
    }

    @Test
    void inputMessagesCanCarryThinkingForOnlyTheActiveToolLoop() {
        Msg message = Msg.builder().role(MsgRole.ASSISTANT)
                .content(List.of(ThinkingBlock.builder().thinking("reasoning-marker").build()))
                .build();

        assertThat(message.getContent()).hasSize(1);
        assertThat(message.getContent().get(0)).isInstanceOf(ThinkingBlock.class);
        assertThat(ThinkingContentFilter.filterResponse(ChatResponse.builder().id("id")
                .content(List.of(message.getContent().get(0))).usage(new ChatUsage(0, 0, 0.0))
                .finishReason("stop").build()).getContent()).isEmpty();
    }
}
