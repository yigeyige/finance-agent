package cn.com.crc.ems.finance.agent.packet.application;

import cn.com.crc.ems.finance.agent.evidence.domain.EffectiveClass;

/** 批准范围内对象的发布前重查快照。 */
public record ScopeSnapshot(String objectKey, String fieldsDigest, EffectiveClass effectiveClass,
        String sourceVersion, String ruleSetVersion, String templateVersion) {

    public ScopeSnapshot {
        if (blank(objectKey) || blank(fieldsDigest) || effectiveClass == null || blank(sourceVersion)
                || blank(ruleSetVersion) || blank(templateVersion)) {
            throw new IllegalArgumentException("scope snapshot is invalid");
        }
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
