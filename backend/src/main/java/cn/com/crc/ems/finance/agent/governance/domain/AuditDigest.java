package cn.com.crc.ems.finance.agent.governance.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

/** 审计 payload 的规范化与摘要计算规则。 */
public final class AuditDigest {

    /** 当前审计规范化规则版本。 */
    public static final String VERSION = "audit-v1";

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .build();

    private AuditDigest() {
    }

    /** 规范化并校验审计 JSON，同时拒绝禁止留存字段。 */
    public static VersionedJson canonicalize(VersionedJson payload) {
        if (payload == null) {
            throw new IllegalArgumentException("payload is required");
        }
        try {
            JsonNode parsed = JSON_MAPPER.readTree(payload.json().trim());
            rejectForbiddenFields(parsed);
            String canonical = JSON_MAPPER.writeValueAsString(sort(parsed));
            return new VersionedJson(payload.schemaVersion(), canonical);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("payload must be valid JSON", exception);
        }
    }

    /** 计算规范化 JSON 的 SHA-256 摘要。 */
    public static String payloadDigest(VersionedJson payload) {
        return sha256(canonicalize(payload).json());
    }

    /** 计算固定事件字段的 SHA-256 摘要。 */
    public static String eventDigest(UUID taskId, UUID actorUserId, String eventType, String targetType,
            String targetId, String canonicalizationVersion, String payloadDigest, String occurredAt,
            String prevEventDigest) {
        return sha256(join(taskId, actorUserId, eventType, targetType, targetId, canonicalizationVersion,
                payloadDigest, occurredAt, prevEventDigest));
    }

    /** 计算文本 SHA-256 摘要。 */
    public static String sha256(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value is required");
        }
        try {
            return java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private static JsonNode sort(JsonNode node) {
        if (node.isObject()) {
            ObjectNode sorted = JsonNodeFactory.instance.objectNode();
            List<String> names = new ArrayList<>();
            node.fieldNames().forEachRemaining(names::add);
            Collections.sort(names);
            for (String name : names) {
                sorted.set(name, sort(node.get(name)));
            }
            return sorted;
        }
        if (node.isArray()) {
            ArrayNode sorted = JsonNodeFactory.instance.arrayNode();
            node.elements().forEachRemaining(value -> sorted.add(sort(value)));
            return sorted;
        }
        return node;
    }

    private static void rejectForbiddenFields(JsonNode node) {
        if (node.isObject()) {
            Iterator<String> names = node.fieldNames();
            while (names.hasNext()) {
                String name = names.next();
                String normalized = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
                if (normalized.contains("rawresponse") || normalized.contains("credential")
                        || normalized.contains("password") || normalized.contains("secret")
                        || normalized.contains("token") || normalized.contains("reasoningcontent")
                        || normalized.equals("reasoning")) {
                    throw new IllegalArgumentException("payload contains forbidden field: " + name);
                }
                rejectForbiddenFields(node.get(name));
            }
        } else if (node.isArray()) {
            node.elements().forEachRemaining(AuditDigest::rejectForbiddenFields);
        }
    }

    private static String join(Object... values) {
        StringBuilder result = new StringBuilder();
        for (Object value : values) {
            String text = value == null ? "<null>" : value.toString();
            result.append(text.length()).append(':').append(text).append('|');
        }
        return result.toString();
    }
}
