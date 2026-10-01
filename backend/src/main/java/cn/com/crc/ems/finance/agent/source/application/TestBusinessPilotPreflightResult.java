package cn.com.crc.ems.finance.agent.source.application;

import java.util.List;

/**
 *
 * @Description: 汇总固定测试业务样本的试点覆盖、未知边界和人工确认负担。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
public record TestBusinessPilotPreflightResult(String referenceSampleVersion, int coveredSamples, int unknownSamples,
        int expectedManualConfirmations, List<TestBusinessPilotIssue> issues) {

    /**
     *
     * @description: 校验预检摘要与问题项数量一致，防止缺失非确认状态。
     *
     * @param: referenceSampleVersion 固定参考样本版本。
     * @param: coveredSamples 已纳入预检的样本数量。
     * @param: unknownSamples 黄金事实为 UNKNOWN 的样本数量。
     * @param: expectedManualConfirmations 预期需要人工确认的边界样本数量。
     * @param: issues 非当前确认状态形成的问题项。
     * @throws: IllegalArgumentException 版本、计数或问题项不符合预检契约时抛出。
     * @author: yigeyige99
     */
    public TestBusinessPilotPreflightResult {
        if (referenceSampleVersion == null || referenceSampleVersion.isBlank() || coveredSamples < 0 || unknownSamples < 0
                || expectedManualConfirmations < 0 || issues == null || expectedManualConfirmations != issues.size()
                || unknownSamples > expectedManualConfirmations || expectedManualConfirmations > coveredSamples) {
            throw new IllegalArgumentException("test business pilot preflight result is invalid");
        }
        issues = List.copyOf(issues);
    }
}
