package cn.com.crc.ems.finance.agent.rules.application;

import java.util.List;

/**
 *
 * @Description: 规则输入的 canonical JSON 摘要和稳定快照顺序。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record NormalizedRuleInput(List<ApprovedSnapshot> snapshots, String inputDigest, String snapshotIds) {

    public NormalizedRuleInput {
        if (snapshots == null || snapshots.isEmpty() || inputDigest == null || inputDigest.isBlank()
                || snapshotIds == null || snapshotIds.isBlank()) {
            throw new IllegalArgumentException("normalized rule input is invalid");
        }
        snapshots = List.copyOf(snapshots);
    }
}
