package cn.com.crc.ems.finance.agent.packet.application;

import java.util.Set;
import java.util.EnumSet;

/**
 *
 * @Description: 单个范围对象的重查差异。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ScopeRecheckDiff(String objectKey, Set<ScopeChangeType> changes) {

    public ScopeRecheckDiff {
        if (objectKey == null || objectKey.isBlank() || changes == null || changes.isEmpty()) {
            throw new IllegalArgumentException("scope recheck diff is invalid");
        }
        changes = EnumSet.copyOf(changes);
    }
}
