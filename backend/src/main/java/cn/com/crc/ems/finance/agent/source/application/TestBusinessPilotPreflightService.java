package cn.com.crc.ems.finance.agent.source.application;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 *
 * @Description: 仅在 local/test 环境汇总固定参考样本的只读试点预检结果。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
@Service
@Profile({"local", "test"})
public class TestBusinessPilotPreflightService {

    private final TestBusinessReferenceSampleCatalog referenceSampleCatalog;

    /**
     *
     * @description: 注入已冻结的测试业务多类型参考样本目录。
     *
     * @param: referenceSampleCatalog 仅 local/test 可用的固定虚构样本目录。
     * @throws: IllegalArgumentException 目录缺失时抛出。
     * @author: yigeyige99
     */
    public TestBusinessPilotPreflightService(TestBusinessReferenceSampleCatalog referenceSampleCatalog) {
        if (referenceSampleCatalog == null) {
            throw new IllegalArgumentException("test business reference sample catalog is required");
        }
        this.referenceSampleCatalog = referenceSampleCatalog;
    }

    /**
     *
     * @description: 以固定黄金事实计算覆盖、未知和需要人工确认的试点边界。
     *
     * @return: 不访问外部系统且不写入任何来源的预检摘要。
     * @author: yigeyige99
     */
    public TestBusinessPilotPreflightResult preflight() {
        List<TestBusinessReferenceSample> samples = referenceSampleCatalog.samples();
        List<TestBusinessPilotIssue> issues = samples.stream().filter(sample -> !sample.expectedState().isConfirmedCurrent())
                .map(this::toIssue).toList();
        int unknownSamples = (int) samples.stream()
                .filter(sample -> sample.expectedState() == TestBusinessReferenceState.UNKNOWN).count();
        return new TestBusinessPilotPreflightResult(TestBusinessReferenceSampleCatalog.VERSION, samples.size(), unknownSamples,
                issues.size(), issues);
    }

    /**
     *
     * @description: 将非当前确认的测试样本转换为不可自动关闭的人工复核问题项。
     *
     * @param: sample 固定虚构参考样本。
     * @return: 包含测试状态边界的试点问题项。
     * @author: yigeyige99
     */
    private TestBusinessPilotIssue toIssue(TestBusinessReferenceSample sample) {
        return new TestBusinessPilotIssue(sample.sampleId(), sample.expectedState(), switch (sample.expectedState()) {
            case FUTURE -> "未来生效测试样本，不作为当前确认事实";
            case UNKNOWN -> "未知测试样本，需要人工复核";
            case NO_MATCH -> "无匹配测试样本，不得回退为确认事实";
            case CURRENT -> throw new IllegalArgumentException("current test sample does not require pilot issue");
        });
    }
}
