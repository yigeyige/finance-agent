package cn.com.crc.ems.finance.agent.governance.domain;

import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

/** 审计 payload 的规范化与摘要计算规则。 */
public final class AuditDigest {

    /** 当前审计规范化规则版本。 */
    public static final String VERSION = "audit-v1";

    private AuditDigest() {
    }

    /** 规范化并校验审计 JSON，同时拒绝禁止留存字段。 */
    public static VersionedJson canonicalize(VersionedJson payload) {
        if (payload == null) {
            throw new IllegalArgumentException("payload is required");
        }
        JsonNode parsed = CanonicalJsonCodec.parse(payload.json());
        rejectForbiddenFields(parsed);
        return new VersionedJson(payload.schemaVersion(), CanonicalJsonCodec.canonicalize(parsed));
    }

    /** 计算规范化 JSON 的 SHA-256 摘要。 */
    public static String payloadDigest(VersionedJson payload) {
        return Sha256Digest.hex(canonicalize(payload).json());
    }

    /** 计算固定事件字段的 SHA-256 摘要。 */
    public static String eventDigest(UUID taskId, UUID actorUserId, String eventType, String targetType,
            String targetId, String canonicalizationVersion, String payloadDigest, String occurredAt,
            String prevEventDigest) {
        return Sha256Digest.hex(join(taskId, actorUserId, eventType, targetType, targetId, canonicalizationVersion,
                payloadDigest, occurredAt, prevEventDigest));
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
