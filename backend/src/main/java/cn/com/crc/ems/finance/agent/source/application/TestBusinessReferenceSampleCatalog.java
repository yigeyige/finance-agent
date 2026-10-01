package cn.com.crc.ems.finance.agent.source.application;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 *
 * @Description: 在 local/test 环境提供版本化多类型虚构参考样本与黄金事实。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
@Component
@Profile({"local", "test"})
public class TestBusinessReferenceSampleCatalog {

    public static final String VERSION = "test-business-reference-samples-v1";

    private static final List<TestBusinessReferenceSample> SAMPLES = List.of(
            sample("TST-REF-001", "TST-EMP-001", "TST-POS-ACCOUNTING", "TEST_OPERATOR", "TEST_NODE_REVIEW",
                    "ORG-TEST-A", TestBusinessReferenceState.CURRENT),
            sample("TST-REF-002", "TST-EMP-002", "TST-POS-ACCOUNTING", "TEST_VIEWER", "TEST_NODE_SUBMIT",
                    "ORG-TEST-A", TestBusinessReferenceState.FUTURE),
            sample("TST-REF-003", "TST-EMP-003", "TST-POS-CONTROL", "TEST_REVIEWER", "TEST_NODE_REVIEW",
                    "ORG-TEST-B", TestBusinessReferenceState.UNKNOWN),
            sample("TST-REF-004", "TST-EMP-404", "TST-POS-NONE", "TEST_NO_ROLE", "TEST_NODE_NONE",
                    "ORG-TEST-NONE", TestBusinessReferenceState.NO_MATCH));

    /**
     *
     * @description: 返回固定顺序的全部多类型测试参考样本。
     *
     * @return: 不可变的黄金事实样本列表。
     * @author: yigeyige99
     */
    public List<TestBusinessReferenceSample> samples() {
        return SAMPLES;
    }

    /**
     *
     * @description: 按稳定样本标识读取对应的黄金事实。
     *
     * @param: sampleId 测试样本标识。
     * @return: 匹配的固定测试样本；未登记标识返回空。
     * @author: yigeyige99
     */
    public Optional<TestBusinessReferenceSample> findBySampleId(String sampleId) {
        if (sampleId == null || sampleId.isBlank()) {
            return Optional.empty();
        }
        return SAMPLES.stream().filter(sample -> sample.sampleId().equals(sampleId)).findFirst();
    }

    private static TestBusinessReferenceSample sample(String sampleId, String employeeId, String positionCode,
            String roleCode, String processNodeCode, String organizationScope, TestBusinessReferenceState state) {
        return new TestBusinessReferenceSample(sampleId, employeeId, positionCode, roleCode, processNodeCode,
                organizationScope, state);
    }
}
