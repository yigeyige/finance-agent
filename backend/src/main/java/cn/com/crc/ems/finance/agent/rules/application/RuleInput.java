package cn.com.crc.ems.finance.agent.rules.application;

import java.util.List;

/**
 *
 * @Description: 确定性规则的获准快照输入集合。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record RuleInput(List<ApprovedSnapshot> snapshots) {

    public RuleInput {
        if (snapshots == null || snapshots.isEmpty()) {
            throw new IllegalArgumentException("rule snapshots are required");
        }
        snapshots = List.copyOf(snapshots);
    }
}
