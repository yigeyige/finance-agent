package cn.com.crc.ems.finance.agent.harness.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @Description: 业务处理模型回复及完整结构化草稿，附带附件请求和继续分析标志。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-08 21:10:00
 */
public record BusinessGuidanceSuggestion(String reply, BusinessHandlingDraft draft, List<String> requestedAttachments,
        boolean needMoreEvidence, boolean needTableReselection) {

    private static final Set<String> FIELDS = Set.of("reply", "draft", "requestedAttachments",
            "needMoreEvidence", "needTableReselection");

    public BusinessGuidanceSuggestion(String reply, BusinessHandlingDraft draft) {
        this(reply, draft, List.of(), false, false);
    }

    public BusinessGuidanceSuggestion(String reply, BusinessHandlingDraft draft, List<String> requestedAttachments) {
        this(reply, draft, requestedAttachments, false, false);
    }

    public BusinessGuidanceSuggestion {
        if (reply == null || reply.isBlank() || reply.length() > 2000 || draft == null) {
            throw new IllegalArgumentException("business guidance is invalid");
        }
        requestedAttachments = requestedAttachments == null ? List.of() : List.copyOf(requestedAttachments);
        if (requestedAttachments.size() > 2 || requestedAttachments.stream()
                .anyMatch(value -> value == null || value.isBlank() || value.length() > 255)) {
            throw new IllegalArgumentException("requestedAttachments is invalid");
        }
    }

    /**
     *
     * @description: 从严格模型 JSON 读取会话回复、完整业务草稿、附件请求和继续分析标志。
     *
     * @param: value 模型返回 JSON
     * @return: 经校验的业务处理建议
     * @throws: BusinessModelOutputException 输出字段不符合契约时抛出
     * @author: yigeyige99
     */
    public static BusinessGuidanceSuggestion fromJson(JsonNode value) {
        return fromJson(value, new ArrayList<>());
    }

    /**
     *
     * @description: 从严格模型 JSON 读取业务建议，并收集流程结构规范化警告。
     *
     * @param: value 模型返回 JSON
     * @param: warnings 用于收集流程结构规范化说明的可变列表
     * @return: 经校验的业务处理建议
     * @throws: BusinessModelOutputException 输出字段不符合契约时抛出
     * @author: yigeyige99
     */
    public static BusinessGuidanceSuggestion fromJson(JsonNode value, List<String> warnings) {
        if (value == null || !value.isObject() || !value.has("reply") || !value.has("draft")
               ) {
            throw new BusinessModelOutputException("模型返回内容不符合约定结构");
        }
        value.fieldNames().forEachRemaining(field -> {
            if (!FIELDS.contains(field)) {
                throw new BusinessModelOutputException("模型返回内容包含未约定字段");
            }
        });
        JsonNode reply = value.get("reply");
        if (!reply.isTextual() || reply.textValue().isBlank() || reply.textValue().length() > 2000) {
            throw new BusinessModelOutputException("模型返回字段格式不正确: reply");
        }
        JsonNode requested = value.get("requestedAttachments");
        if (requested != null && !requested.isNull() && (!requested.isArray() || requested.size() > 2)) {
            throw new BusinessModelOutputException("模型返回字段格式不正确: requestedAttachments");
        }
        List<String> fileNames = new ArrayList<>();
        if (requested != null && requested.isArray()) requested.forEach(item -> {
            if (!item.isTextual() || item.textValue().isBlank() || item.textValue().length() > 255) {
                throw new BusinessModelOutputException("模型返回字段格式不正确: requestedAttachments");
            }
            fileNames.add(item.textValue());
        });
        boolean needMoreEvidence = value.path("needMoreEvidence")
                .asBoolean(value.path("draft").path("needMoreEvidence").asBoolean(false));
        boolean needTableReselection = value.path("needTableReselection")
                .asBoolean(value.path("draft").path("needTableReselection").asBoolean(false));
        return new BusinessGuidanceSuggestion(reply.textValue(),
                BusinessHandlingDraft.fromJson(value.get("draft"), warnings), fileNames,
                needMoreEvidence, needTableReselection);
    }
}
