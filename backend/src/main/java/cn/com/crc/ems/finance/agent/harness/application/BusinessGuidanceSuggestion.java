package cn.com.crc.ems.finance.agent.harness.application;

import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @Description: 业务处理模型回复及完整结构化草稿，供页面安全回填。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-08 21:10:00
 */
public record BusinessGuidanceSuggestion(String reply, BusinessHandlingDraft draft) {

    private static final Set<String> FIELDS = Set.of("reply", "draft");

    public BusinessGuidanceSuggestion {
        if (reply == null || reply.isBlank() || reply.length() > 2000 || draft == null) {
            throw new IllegalArgumentException("business guidance is invalid");
        }
    }

    /**
     *
     * @description: 从严格模型 JSON 读取会话回复与完整业务草稿。
     *
     * @param: value 模型返回 JSON
     * @return: 经校验的业务处理建议
     * @throws: BusinessModelException 输出字段不符合契约时抛出
     * @author: yigeyige99
     */
    public static BusinessGuidanceSuggestion fromJson(JsonNode value) {
        if (value == null || !value.isObject() || value.size() != FIELDS.size() || !value.has("reply")
                || !value.has("draft")) {
            throw new BusinessModelException("模型返回内容不符合约定结构");
        }
        value.fieldNames().forEachRemaining(field -> {
            if (!FIELDS.contains(field)) {
                throw new BusinessModelException("模型返回内容包含未约定字段");
            }
        });
        JsonNode reply = value.get("reply");
        if (!reply.isTextual() || reply.textValue().isBlank() || reply.textValue().length() > 2000) {
            throw new BusinessModelException("模型返回字段格式不正确: reply");
        }
        return new BusinessGuidanceSuggestion(reply.textValue(), BusinessHandlingDraft.fromJson(value.get("draft")));
    }
}
