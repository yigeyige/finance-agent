package cn.com.crc.ems.finance.agent.packet.application;

import cn.com.crc.ems.finance.agent.evidence.domain.EffectiveClass;

/**
 *
 * @Description: 批准范围内对象的发布前重查快照。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
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
