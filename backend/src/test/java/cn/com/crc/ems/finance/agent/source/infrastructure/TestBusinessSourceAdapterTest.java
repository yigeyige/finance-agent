package cn.com.crc.ems.finance.agent.source.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import cn.com.crc.ems.finance.agent.source.application.ApprovedQuery;
import cn.com.crc.ems.finance.agent.source.application.SourceAdapterErrorCode;
import cn.com.crc.ems.finance.agent.source.application.SourceAdapterUnavailableException;
import cn.com.crc.ems.finance.agent.tool.application.TrustedFieldProjector;

/**
 *
 * @Description: 验证测试业务只读源适配器的白名单、稳定分页、失败关闭和环境隔离契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
class TestBusinessSourceAdapterTest {

    private final TestBusinessSourceAdapter adapter = new TestBusinessSourceAdapter(new TrustedFieldProjector());

    @Test
    void returnsStableProjectedPageForRegisteredQuery() {
        var first = adapter.query(query(Map.of("page", "1", "size", "2")));
        var second = adapter.query(query(Map.of("page", "1", "size", "2")));

        assertThat(first).isEqualTo(second);
        assertThat(first.records()).hasSize(2).allSatisfy(record -> assertThat(record).containsOnlyKeys("testEmployeeId",
                "testRoleCode", "testOrganizationScope", "effectiveState"));
        assertThat(first.records()).extracting(record -> record.get("testEmployeeId")).containsExactly("TST-EMP-001",
                "TST-EMP-002");
        assertThat(first.sourceWatermark()).isEqualTo(TestBusinessSourceFixture.VERSION);
        assertThat(first.resultDigest()).hasSize(64);
    }

    @Test
    void rejectsUnregisteredQueryWithoutSourceFallback() {
        assertThatThrownBy(() -> adapter.query(query(Map.of("url", "https://invalid.example"))))
                .isInstanceOfSatisfying(SourceAdapterUnavailableException.class,
                        error -> assertThat(error.code()).isEqualTo(SourceAdapterErrorCode.SOURCE_QUERY_REJECTED));
        assertThatThrownBy(() -> adapter.query(new ApprovedQuery("wrong-catalog", TestBusinessSourceAdapter.TEMPLATE, Map.of())))
                .isInstanceOfSatisfying(SourceAdapterUnavailableException.class,
                        error -> assertThat(error.code()).isEqualTo(SourceAdapterErrorCode.SOURCE_CATALOG_DRIFT));
    }

    @Test
    void rejectsFixtureDriftAndUnavailableFixture() {
        var driftedFixture = new TestBusinessSourceFixture("test-business-fixture-v2", List.of(record()));
        var drifted = new TestBusinessSourceAdapter(new TrustedFieldProjector(), driftedFixture);
        var unavailable = new TestBusinessSourceAdapter(new TrustedFieldProjector(), null);

        assertThatThrownBy(() -> drifted.query(query(Map.of()))).isInstanceOfSatisfying(SourceAdapterUnavailableException.class,
                error -> assertThat(error.code()).isEqualTo(SourceAdapterErrorCode.SOURCE_CATALOG_DRIFT));
        assertThatThrownBy(() -> unavailable.query(query(Map.of()))).isInstanceOfSatisfying(SourceAdapterUnavailableException.class,
                error -> assertThat(error.code()).isEqualTo(SourceAdapterErrorCode.SOURCE_UNAVAILABLE));
    }

    @Test
    void registersOnlyForLocalAndTestProfiles() {
        try (var local = context("local"); var production = context("prod")) {
            assertThat(local.getBeansOfType(TestBusinessSourceAdapter.class)).hasSize(1);
            assertThat(production.getBeansOfType(TestBusinessSourceAdapter.class)).isEmpty();
        }
    }

    private static ApprovedQuery query(Map<String, String> parameters) {
        return new ApprovedQuery(TestBusinessSourceAdapter.CATALOG_VERSION, TestBusinessSourceAdapter.TEMPLATE, parameters);
    }

    private static Map<String, String> record() {
        return Map.of("testEmployeeId", "TST-EMP-999", "testRoleCode", "TEST_OPERATOR", "testOrganizationScope", "ORG-TEST-C",
                "effectiveState", "CURRENT");
    }

    private static AnnotationConfigApplicationContext context(String profile) {
        var context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles(profile);
        context.register(TrustedFieldProjector.class, TestBusinessSourceAdapter.class);
        context.refresh();
        return context;
    }
}
