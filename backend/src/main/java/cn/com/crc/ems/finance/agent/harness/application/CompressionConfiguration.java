package cn.com.crc.ems.finance.agent.harness.application;

import java.util.Set;

/** Context Compression 的长度、引用类型和未知项完整性配置。 */
public record CompressionConfiguration(int maxSummaryLength,
        Set<CompressionReferenceType> allowedReferenceTypes, boolean requireUnknownItems) {

    public CompressionConfiguration {
        if (maxSummaryLength < 1 || maxSummaryLength > 100_000
                || allowedReferenceTypes == null || allowedReferenceTypes.isEmpty()) {
            throw new IllegalArgumentException("compression configuration is invalid");
        }
        allowedReferenceTypes = Set.copyOf(allowedReferenceTypes);
    }

    public static CompressionConfiguration defaults() {
        return new CompressionConfiguration(2_000, Set.of(CompressionReferenceType.values()), true);
    }
}
