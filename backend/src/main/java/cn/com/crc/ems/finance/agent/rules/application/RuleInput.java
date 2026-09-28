package cn.com.crc.ems.finance.agent.rules.application;

import java.util.List;

/** 确定性规则的获准快照输入集合。 */
public record RuleInput(List<ApprovedSnapshot> snapshots) {

    public RuleInput {
        if (snapshots == null || snapshots.isEmpty()) {
            throw new IllegalArgumentException("rule snapshots are required");
        }
        snapshots = List.copyOf(snapshots);
    }
}
