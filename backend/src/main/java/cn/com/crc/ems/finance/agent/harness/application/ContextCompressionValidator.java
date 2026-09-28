package cn.com.crc.ems.finance.agent.harness.application;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** 校验压缩引用存在性、digest、字段白名单和未知项完整性。 */
public final class ContextCompressionValidator {

    private ContextCompressionValidator() {
    }

    /**
     * 以 authoritative 引用目录校验模型不可控的压缩输出。
     *
     * @param summary 压缩摘要。
     * @param authoritativeReferences 已确认的引用目录。
     * @param expectedUnknownItems 压缩前必须保留的未知项。
     * @param configuration 压缩边界配置。
     * @return 通过校验的原摘要。
     * @throws ContextCompressionException 引用伪造、摘要篡改或未知项丢失时抛出。
     * @author yigeyige99
     */
    public static ContextCompressionSummary validate(ContextCompressionSummary summary,
            Map<String, CompressionReference> authoritativeReferences, Set<String> expectedUnknownItems,
            CompressionConfiguration configuration) {
        if (summary == null || authoritativeReferences == null || expectedUnknownItems == null
                || configuration == null) {
            throw reject("summary, reference catalog, unknown items and configuration are required");
        }
        Set<String> seen = new HashSet<>();
        for (CompressionReference reference : summary.references()) {
            if (!configuration.allowedReferenceTypes().contains(reference.type())) {
                throw reject("reference type is not allowed");
            }
            if (!seen.add(reference.referenceId())) {
                throw reject("duplicate reference ID");
            }
            CompressionReference authoritative = authoritativeReferences.get(reference.referenceId());
            if (authoritative == null || authoritative.type() != reference.type()
                    || !authoritative.digest().equalsIgnoreCase(reference.digest())) {
                throw reject("reference ID or digest is not authoritative");
            }
        }
        summary.summaries().forEach((referenceId, text) -> {
            if (!seen.contains(referenceId) || text == null || text.isBlank()
                    || text.length() > configuration.maxSummaryLength()) {
                throw reject("summary must target a known reference and stay within the configured limit");
            }
        });
        Set<String> expected = Set.copyOf(expectedUnknownItems);
        if (configuration.requireUnknownItems() && !summary.unknownItems().equals(expected)) {
            throw reject("unknown items were changed or dropped");
        }
        return summary;
    }

    private static ContextCompressionException reject(String reason) {
        return new ContextCompressionException(reason);
    }
}
