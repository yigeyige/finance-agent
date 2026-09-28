package cn.com.crc.ems.finance.agent.harness.application;

/** 只保存业务对象 ID 与 authoritative digest，不承载对象原文。 */
public record CompressionReference(CompressionReferenceType type, String referenceId, String digest) {

    public CompressionReference {
        if (type == null || blank(referenceId) || !validDigest(digest)) {
            throw new IllegalArgumentException("compression reference is invalid");
        }
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank() || value.length() > 200;
    }

    private static boolean validDigest(String value) {
        return value != null && value.matches("[0-9a-fA-F]{64}");
    }
}
