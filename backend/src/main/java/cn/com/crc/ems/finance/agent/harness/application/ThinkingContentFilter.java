package cn.com.crc.ems.finance.agent.harness.application;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.agentscope.core.message.ContentBlock;
import io.agentscope.core.message.ThinkingBlock;
import io.agentscope.core.message.ToolUseBlock;
import io.agentscope.core.model.ChatResponse;

/**
 *
 * @Description: reasoning 禁止字段过滤器，统一用于模型响应、事件、异常和持久化边界。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public final class ThinkingContentFilter {

    private static final Set<String> FORBIDDEN_FIELDS = Set.of(
            "reasoning_content", "reasoningContent", "thinking", "reasoningDetails", "reasoning_detail");

    private ThinkingContentFilter() {
    }

    /**
     * 递归复制 JSON 并移除所有 reasoning 字段，避免修改模型原始对象。
     *
     * @param value 待过滤 JSON。
     * @return 不含 reasoning 字段的深拷贝。
     * @author yigeyige99
     */
    public static JsonNode filter(JsonNode value) {
        if (value == null) {
            return JsonNodeFactory.instance.nullNode();
        }
        if (value.isObject()) {
            ObjectNode result = JsonNodeFactory.instance.objectNode();
            value.fields().forEachRemaining(entry -> {
                if (!isForbiddenField(entry.getKey())) {
                    result.set(entry.getKey(), filter(entry.getValue()));
                }
            });
            return result;
        }
        if (value.isArray()) {
            ArrayNode result = JsonNodeFactory.instance.arrayNode();
            value.forEach(item -> result.add(filter(item)));
            return result;
        }
        return value.deepCopy();
    }

    /**
     * 删除 AgentScope 的 ThinkingBlock，同时保留最终文本和工具调用，并清理工具 metadata。
     *
     * @param response AgentScope 模型响应。
     * @return 可向事件、日志或持久化边界传递的响应。
     * @author yigeyige99
     */
    public static ChatResponse filterResponse(ChatResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("response is required");
        }
        List<ContentBlock> blocks = response.getContent() == null ? List.of()
                : response.getContent().stream()
                        .filter(block -> !(block instanceof ThinkingBlock))
                        .map(ThinkingContentFilter::filterToolBlock)
                        .toList();
        Map<String, Object> metadata = filterMap(response.getMetadata());
        return ChatResponse.builder().id(response.getId()).content(blocks).usage(response.getUsage())
                .metadata(metadata).finishReason(response.getFinishReason()).build();
    }

    public static boolean isForbiddenField(String fieldName) {
        return FORBIDDEN_FIELDS.contains(fieldName);
    }

    private static ContentBlock filterToolBlock(ContentBlock block) {
        if (!(block instanceof ToolUseBlock tool)) {
            return block;
        }
        return ToolUseBlock.builder().id(tool.getId()).name(tool.getName()).input(filterMap(tool.getInput()))
                .content(tool.getContent()).metadata(filterMap(tool.getMetadata())).state(tool.getState()).build();
    }

    private static Map<String, Object> filterMap(Map<String, ?> source) {
        if (source == null || source.isEmpty()) {
            return Map.of();
        }
        Map<String, Object> filtered = new LinkedHashMap<>();
        source.forEach((key, value) -> {
            if (!isForbiddenField(key)) {
                filtered.put(key, filterValue(value));
            }
        });
        return Collections.unmodifiableMap(filtered);
    }

    private static Object filterValue(Object value) {
        if (value instanceof JsonNode node) {
            return filter(node);
        }
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> converted = new LinkedHashMap<>();
            map.forEach((key, item) -> {
                if (key != null && !isForbiddenField(key.toString())) {
                    converted.put(key.toString(), filterValue(item));
                }
            });
            return Collections.unmodifiableMap(converted);
        }
        if (value instanceof List<?> list) {
            return list.stream().map(ThinkingContentFilter::filterValue).toList();
        }
        return value;
    }
}
