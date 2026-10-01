package cn.com.crc.ems.finance.agent.source.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @Description: 验证测试业务多类型参考样本的版本、黄金事实和环境隔离边界。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
class TestBusinessReferenceSampleCatalogTest {

    private final TestBusinessReferenceSampleCatalog catalog = new TestBusinessReferenceSampleCatalog();

    @Test
    void suppliesMultiTypeSamplesForAllRequiredBoundaries() {
        assertThat(TestBusinessReferenceSampleCatalog.VERSION).isEqualTo("test-business-reference-samples-v1");
        assertThat(catalog.samples()).hasSize(4).extracting(TestBusinessReferenceSample::expectedState)
                .containsExactly(TestBusinessReferenceState.CURRENT, TestBusinessReferenceState.FUTURE,
                        TestBusinessReferenceState.UNKNOWN, TestBusinessReferenceState.NO_MATCH);
        assertThat(catalog.samples()).allSatisfy(sample -> {
            assertThat(sample.testEmployeeId()).startsWith("TST-EMP-");
            assertThat(sample.testPositionCode()).startsWith("TST-POS-");
            assertThat(sample.testRoleCode()).startsWith("TEST_");
            assertThat(sample.testProcessNodeCode()).startsWith("TEST_NODE_");
            assertThat(sample.testOrganizationScope()).startsWith("ORG-TEST-");
        });
    }

    @Test
    void preservesFutureUnknownAndNoMatchAsNonConfirmedFacts() {
        assertThat(catalog.findBySampleId("TST-REF-001")).map(TestBusinessReferenceSample::expectedState)
                .contains(TestBusinessReferenceState.CURRENT);
        assertThat(catalog.samples()).filteredOn(sample -> sample.expectedState() != TestBusinessReferenceState.CURRENT)
                .allSatisfy(sample -> assertThat(sample.expectedState().isConfirmedCurrent()).isFalse());
        assertThat(catalog.findBySampleId("TST-REF-MISSING")).isEmpty();
    }

    @Test
    void rejectsNonTestIdentifiersAndRegistersOnlyForLocalOrTestProfiles() {
        assertThatThrownBy(() -> new TestBusinessReferenceSample("EMP-001", "TST-EMP-001", "TST-POS-ACCOUNTING",
                "TEST_OPERATOR", "TEST_NODE_REVIEW", "ORG-TEST-A", TestBusinessReferenceState.CURRENT))
                .isInstanceOf(IllegalArgumentException.class);
        try (var local = context("local"); var production = context("prod")) {
            assertThat(local.getBeansOfType(TestBusinessReferenceSampleCatalog.class)).hasSize(1);
            assertThat(production.getBeansOfType(TestBusinessReferenceSampleCatalog.class)).isEmpty();
        }
    }

    private static AnnotationConfigApplicationContext context(String profile) {
        var context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles(profile);
        context.register(TestBusinessReferenceSampleCatalog.class);
        context.refresh();
        return context;
    }
}
