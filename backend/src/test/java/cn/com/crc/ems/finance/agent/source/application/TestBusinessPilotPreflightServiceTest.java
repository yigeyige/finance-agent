package cn.com.crc.ems.finance.agent.source.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @Description: 验证测试业务试点预检的覆盖统计、非确认边界和环境隔离。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
class TestBusinessPilotPreflightServiceTest {

    private final TestBusinessPilotPreflightService service = new TestBusinessPilotPreflightService(
            new TestBusinessReferenceSampleCatalog());

    @Test
    void summarizesCoverageUnknownAndManualReviewBoundaries() {
        TestBusinessPilotPreflightResult result = service.preflight();

        assertThat(result.referenceSampleVersion()).isEqualTo(TestBusinessReferenceSampleCatalog.VERSION);
        assertThat(result.coveredSamples()).isEqualTo(4);
        assertThat(result.unknownSamples()).isEqualTo(1);
        assertThat(result.expectedManualConfirmations()).isEqualTo(3);
        assertThat(result.issues()).extracting(TestBusinessPilotIssue::sampleId).containsExactly("TST-REF-002", "TST-REF-003",
                "TST-REF-004");
        assertThat(result.issues()).extracting(TestBusinessPilotIssue::state).containsExactly(TestBusinessReferenceState.FUTURE,
                TestBusinessReferenceState.UNKNOWN, TestBusinessReferenceState.NO_MATCH);
    }

    @Test
    void keepsNonCurrentSamplesOutOfConfirmedFactsAndRejectsInvalidIssues() {
        assertThat(service.preflight().issues()).allSatisfy(issue -> assertThat(issue.state().isConfirmedCurrent()).isFalse());
        assertThatThrownBy(() -> new TestBusinessPilotIssue("TST-REF-001", TestBusinessReferenceState.CURRENT, "invalid"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TestBusinessPilotIssue("EMP-001", TestBusinessReferenceState.UNKNOWN, "invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void registersOnlyForLocalAndTestProfiles() {
        try (var local = context("local"); var production = context("prod")) {
            assertThat(local.getBeansOfType(TestBusinessPilotPreflightService.class)).hasSize(1);
            assertThat(production.getBeansOfType(TestBusinessPilotPreflightService.class)).isEmpty();
        }
    }

    private static AnnotationConfigApplicationContext context(String profile) {
        var context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles(profile);
        context.register(TestBusinessReferenceSampleCatalog.class, TestBusinessPilotPreflightService.class);
        context.refresh();
        return context;
    }
}
