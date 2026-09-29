package cn.com.crc.ems.finance.agent.packet.application;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import cn.com.crc.ems.finance.agent.evidence.domain.CoverageCompleteness;

/** 在决策包结构校验之后约束覆盖度、事实来源和业务影响的表述边界。 */
@Component
public final class PacketWordingValidator {

    private static final Set<String> NON_AUTHORITATIVE_SOURCES = Set.of("RAG", "MEMORY", "MODEL");
    private static final Pattern ABSOLUTE_COVERAGE = Pattern.compile("全量|全部|所有对象|不存在遗漏|full\\s*scope|all\\s+objects",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern DETERMINISTIC_IMPACT = Pattern.compile("实际影响|已造成影响|影响金额|actual\\s+impact|impact\\s+amount",
            Pattern.CASE_INSENSITIVE);

    /**
     * 校验决策包措辞是否与覆盖完整性、事实来源和业务确认状态一致。
     *
     * @param document 待校验的结构化决策包
     * @param completeness 覆盖完整性
     * @param businessImpactConfirmed 业务影响是否已经人工确认
     * @throws IllegalArgumentException 违反措辞边界时抛出
     * @author yigeyige99
     */
    public void validate(JsonNode document, CoverageCompleteness completeness, boolean businessImpactConfirmed) {
        if (document == null || !document.isObject() || completeness == null) {
            throw new IllegalArgumentException("packet wording input is incomplete");
        }
        if (completeness != CoverageCompleteness.COMPLETE) {
            requireLimitations(document);
            if (ABSOLUTE_COVERAGE.matcher(text(document)).find()) {
                throw new IllegalArgumentException("incomplete coverage cannot claim full scope");
            }
            if (completeness == CoverageCompleteness.UNKNOWN && !hasItems(document.path("facts"), "undetermined")) {
                throw new IllegalArgumentException("unknown coverage requires undetermined facts");
            }
        }
        rejectNonAuthoritativeCurrentFacts(document.path("facts").path("current"));
        if (!businessImpactConfirmed) {
            JsonNode business = document.path("businessConfirmations");
            if (!hasItems(business, "pendingItems")) {
                throw new IllegalArgumentException("unconfirmed business impact requires pending items");
            }
            String impactText = text(document.path("recommendations")) + text(document.path("computations"));
            if (DETERMINISTIC_IMPACT.matcher(impactText).find()) {
                throw new IllegalArgumentException("unconfirmed business impact must remain qualified");
            }
        }
    }

    private static void requireLimitations(JsonNode document) {
        JsonNode limitations = document.get("limitations");
        if (limitations == null || !limitations.isTextual() || limitations.asText().isBlank()) {
            throw new IllegalArgumentException("incomplete coverage requires limitations");
        }
    }

    private static void rejectNonAuthoritativeCurrentFacts(JsonNode currentFacts) {
        if (currentFacts == null || !currentFacts.isArray()) {
            return;
        }
        for (JsonNode fact : currentFacts) {
            String source = firstText(fact, "sourceKind", "basis", "origin");
            if (source != null && NON_AUTHORITATIVE_SOURCES.contains(source.toUpperCase(Locale.ROOT))) {
                throw new IllegalArgumentException("RAG, memory or model content cannot be a current fact");
            }
        }
    }

    private static String firstText(JsonNode node, String... names) {
        for (String name : names) {
            JsonNode value = node.get(name);
            if (value != null && value.isTextual() && !value.asText().isBlank()) {
                return value.asText();
            }
        }
        return null;
    }

    private static boolean hasItems(JsonNode parent, String name) {
        JsonNode value = parent == null ? null : parent.get(name);
        return value != null && value.isArray() && !value.isEmpty();
    }

    private static String text(JsonNode node) {
        if (node == null || node.isNull()) {
            return "";
        }
        if (node.isValueNode()) {
            return node.asText();
        }
        StringBuilder result = new StringBuilder();
        node.elements().forEachRemaining(value -> result.append(text(value)).append('\n'));
        return result.toString();
    }
}
