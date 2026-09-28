package cn.com.crc.ems.finance.agent.evidence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.evidence.application.EffectiveClassifier;
import cn.com.crc.ems.finance.agent.evidence.application.SourceCandidate;
import cn.com.crc.ems.finance.agent.evidence.application.SourceConflictCode;
import cn.com.crc.ems.finance.agent.evidence.application.SourceConflictCoordinator;
import cn.com.crc.ems.finance.agent.evidence.domain.EffectiveClass;
import cn.com.crc.ems.finance.agent.source.application.SourceCatalog;

class EffectiveClassificationTest {

    private static final Instant CHECKED_AT = Instant.parse("2026-09-28T00:00:00Z");

    @Test
    void fixedClockClassifiesCurrentFutureAndExpiredFacts() {
        EffectiveClassifier classifier = new EffectiveClassifier();
        assertThat(classifier.classify(CHECKED_AT, Instant.parse("2026-09-27T00:00:00Z"), null))
                .isEqualTo(EffectiveClass.CURRENT_EFFECTIVE);
        assertThat(classifier.classify(CHECKED_AT, Instant.parse("2026-09-29T00:00:00Z"), null))
                .isEqualTo(EffectiveClass.FUTURE_SCHEDULED);
        assertThat(classifier.classify(CHECKED_AT, Instant.parse("2026-09-01T00:00:00Z"), CHECKED_AT))
                .isEqualTo(EffectiveClass.UNDETERMINED);
    }

    @Test
    void missingAndInvalidTimeAreUndeterminedAndIndependentOfDefaultZone() {
        EffectiveClassifier classifier = new EffectiveClassifier();
        assertThat(classifier.classify(CHECKED_AT, null, null)).isEqualTo(EffectiveClass.UNDETERMINED);
        assertThat(classifier.classify(CHECKED_AT, CHECKED_AT, CHECKED_AT.minusSeconds(1)))
                .isEqualTo(EffectiveClass.UNDETERMINED);
        String original = System.getProperty("user.timezone");
        try {
            System.setProperty("user.timezone", "Asia/Shanghai");
            EffectiveClass shanghai = classifier.classify(CHECKED_AT, CHECKED_AT.minusSeconds(1), null);
            System.setProperty("user.timezone", "UTC");
            EffectiveClass utc = classifier.classify(CHECKED_AT, CHECKED_AT.minusSeconds(1), null);
            assertThat(shanghai).isEqualTo(utc).isEqualTo(EffectiveClass.CURRENT_EFFECTIVE);
        } finally {
            if (original == null) System.clearProperty("user.timezone"); else System.setProperty("user.timezone", original);
        }
    }

    @Test
    void catalogAuthorityResolvesOnlyUniqueHighestSource() {
        SourceCatalog catalog = new SourceCatalog("catalog-v2", Map.of("template", Map.of("page", "integer")),
                Map.of("employee-reference", List.of("ERP", "PORTAL")));
        var candidates = List.of(new SourceCandidate("PORTAL", "employee-reference", "EMP-1", "b"),
                new SourceCandidate("ERP", "employee-reference", "EMP-1", "a"));
        var result = new SourceConflictCoordinator().resolve(catalog, candidates);
        assertThat(result.code()).isEqualTo(SourceConflictCode.RESOLVED);
        assertThat(result.resolvedSourceId()).isEqualTo("ERP");
    }

    @Test
    void unknownRegistrationAndMixedObjectsProduceUnknownConflict() {
        SourceCatalog catalog = new SourceCatalog("catalog-v2", Map.of("template", Map.of("page", "integer")),
                Map.of("employee-reference", List.of("ERP", "PORTAL")));
        var coordinator = new SourceConflictCoordinator();
        assertThat(coordinator.resolve(catalog,
                List.of(new SourceCandidate("ERP", "unknown", "EMP-1", "a"),
                        new SourceCandidate("PORTAL", "unknown", "EMP-1", "b"))).code())
                .isEqualTo(SourceConflictCode.UNKNOWN_SOURCE_CONFLICT);
        assertThat(coordinator.resolve(catalog,
                List.of(new SourceCandidate("ERP", "employee-reference", "EMP-1", "a"),
                        new SourceCandidate("PORTAL", "employee-reference", "EMP-2", "b"))).code())
                .isEqualTo(SourceConflictCode.UNKNOWN_SOURCE_CONFLICT);
    }
}
