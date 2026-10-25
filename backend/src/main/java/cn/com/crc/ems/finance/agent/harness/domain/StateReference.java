package cn.com.crc.ems.finance.agent.harness.domain;

import java.util.Locale;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import cn.com.crc.ems.finance.agent.shared.domain.JsonCodec;

/**
 *
 * @Description: Harness 状态的最小 JSON 引用，负责 schema、摘要和敏感字段边界。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record StateReference(String schemaVersion, String json, String digest) {

    private static final Set<String> FORBIDDEN_KEY_PARTS = Set.of(
            "sourcerawresponse", "rawresponse", "credential", "password", "secret", "token",
            "reasoningcontent", "reasoning");

    public StateReference {
        requireText(schemaVersion, "schemaVersion");
        requireText(json, "json");
        requireDigest(digest);
        new cn.com.crc.ems.finance.agent.task.domain.VersionedJson(schemaVersion, json);
        rejectForbiddenFields(json);
    }

    /** 递归校验状态 JSON，不允许原始响应、凭证、Token 或 reasoning 内容进入持久化引用。 */
    private static void rejectForbiddenFields(String json) {
        try {
            JsonNode node = JsonCodec.mapper().readTree(json);
            rejectForbiddenFields(node);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("state JSON must be valid JSON", exception);
        }
    }

    private static void rejectForbiddenFields(JsonNode node) {
        if (node == null) {
            return;
        }
        if (node.isObject()) {
            var fields = node.fields();
            while (fields.hasNext()) {
                String key = fields.next().getKey();
                String normalized = key.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
                if (FORBIDDEN_KEY_PARTS.stream().anyMatch(normalized::contains)) {
                    throw new IllegalArgumentException("state JSON contains a forbidden field: " + key);
                }
                rejectForbiddenFields(node.get(key));
            }
        } else if (node.isArray()) {
            node.forEach(StateReference::rejectForbiddenFields);
        }
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }

    private static void requireDigest(String value) {
        if (value == null || !value.matches("[0-9a-fA-F]{64}")) {
            throw new IllegalArgumentException("digest must be a 64-character hexadecimal digest");
        }
    }
}
