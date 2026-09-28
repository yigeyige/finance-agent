package cn.com.crc.ems.finance.agent.packet.application;

import java.util.Set;
import java.util.EnumSet;

/** 单个范围对象的重查差异。 */
public record ScopeRecheckDiff(String objectKey, Set<ScopeChangeType> changes) {

    public ScopeRecheckDiff {
        if (objectKey == null || objectKey.isBlank() || changes == null || changes.isEmpty()) {
            throw new IllegalArgumentException("scope recheck diff is invalid");
        }
        changes = EnumSet.copyOf(changes);
    }
}
