package cn.com.crc.ems.finance.agent.evidence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.evidence.application.CoverageEnumerator;
import cn.com.crc.ems.finance.agent.evidence.application.CoveragePage;
import cn.com.crc.ems.finance.agent.evidence.application.CoverageRecordFactory;
import cn.com.crc.ems.finance.agent.evidence.application.CoverageValidator;
import cn.com.crc.ems.finance.agent.evidence.domain.CoverageCompleteness;

class CoverageEnumerationTest {

    private final CoverageEnumerator enumerator = new CoverageEnumerator();
    private final CoverageValidator validator = new CoverageValidator();

    @Test
    void completeTraversalProducesStableDigestAndRecord() {
        var result = enumerator.enumerate((page, token) -> switch (page) {
            case 1 -> page(1, List.of(Map.of("id", "A")), 3, true, "p2");
            case 2 -> page(2, List.of(Map.of("id", "B"), Map.of("id", "C")), 3, false, null);
            default -> throw new IllegalStateException("unexpected page");
        }, 5);

        assertThat(validator.validate(result)).isEqualTo(CoverageCompleteness.COMPLETE);
        assertThat(result.observedTotal()).isEqualTo(3);
        assertThat(result.resultSetDigest()).hasSize(64);

        var record = new CoverageRecordFactory().create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), "template-v1", "condition-digest", "PAGE", result, Instant.parse("2026-09-28T00:00:00Z"),
                UUID.randomUUID());
        assertThat(record.completeness()).isEqualTo(CoverageCompleteness.COMPLETE);
        assertThat(record.expectedTotal()).isEqualTo(3);
        assertThat(record.traversedPaths()).isEqualTo("[\"/records?page=1\",\"/records?page=2\"]");
        assertThat(record.uncoveredPaths()).isEqualTo("[]");
        assertThat(record.failures()).isEqualTo("[]");
    }

    @Test
    void paginationFailureAndMissingPageArePartial() {
        var sourceFailure = enumerator.enumerate((page, token) -> {
            if (page == 1) return page(1, List.of(Map.of("id", "A")), 2, true, "p2");
            throw new IllegalStateException("source unavailable");
        }, 5);
        assertThat(sourceFailure.completeness()).isEqualTo(CoverageCompleteness.PARTIAL);
        assertThat(sourceFailure.failures()).contains("page 2 fetch failed");

        var missingPage = enumerator.enumerate((page, token) -> page(3, List.of(Map.of("id", "A")), 1, false, null), 5);
        assertThat(missingPage.completeness()).isEqualTo(CoverageCompleteness.PARTIAL);
        assertThat(missingPage.uncoveredPaths()).anyMatch(path -> path.contains("expected page 1"));
    }

    @Test
    void duplicateTokenTotalChangeAndWatermarkChangeAreUnknown() {
        var duplicateToken = enumerator.enumerate((page, token) -> page(page,
                List.of(Map.of("id", String.valueOf(page))), 2, true, "same"), 5);
        assertThat(duplicateToken.completeness()).isEqualTo(CoverageCompleteness.UNKNOWN);
        assertThat(duplicateToken.failures()).contains("duplicate continuation token");

        var totalChanged = enumerator.enumerate((page, token) -> page(page,
                List.of(Map.of("id", String.valueOf(page))), page == 1 ? 2L : 3L,
                page == 1, page == 1 ? "p2" : null), 5);
        assertThat(totalChanged.completeness()).isEqualTo(CoverageCompleteness.UNKNOWN);
        assertThat(totalChanged.failures()).contains("expected total changed");

        var watermarkChanged = enumerator.enumerate((page, token) -> new CoveragePage<>(page,
                List.of(Map.of("id", String.valueOf(page))), 1L, page == 1, page == 1 ? "p2" : null,
                page == 1 ? "wm-1" : "wm-2", "/records?page=" + page), 5);
        assertThat(watermarkChanged.completeness()).isEqualTo(CoverageCompleteness.UNKNOWN);
        assertThat(watermarkChanged.failures()).contains("source watermark changed");
    }

    @Test
    void pageRejectsMissingContinuationToken() {
        assertThatThrownBy(() -> new CoveragePage<>(1, List.of("A"), 1L, true, null, "wm", "/records"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static CoveragePage<Map<String, String>> page(int page, List<Map<String, String>> records,
            long total, boolean hasNext, String nextToken) {
        return new CoveragePage<>(page, records, total, hasNext, nextToken, "wm-1", "/records?page=" + page);
    }
}
