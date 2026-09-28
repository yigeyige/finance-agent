package cn.com.crc.ems.finance.agent.evidence.application;

/** 同一业务对象的来源候选摘要。 */
public record SourceCandidate(String sourceId, String objectType, String objectKey, String resultDigest) {

    public SourceCandidate {
        if (blank(sourceId) || blank(objectType) || blank(objectKey) || blank(resultDigest)) {
            throw new IllegalArgumentException("source candidate is invalid");
        }
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
