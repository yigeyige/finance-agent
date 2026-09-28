package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.harness.application.CompressionConfiguration;
import cn.com.crc.ems.finance.agent.harness.application.CompressionReference;
import cn.com.crc.ems.finance.agent.harness.application.CompressionReferenceType;
import cn.com.crc.ems.finance.agent.harness.application.ContextCompressionException;
import cn.com.crc.ems.finance.agent.harness.application.ContextCompressionService;

import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;

class ContextCompressionTest {

    private static final UUID TASK_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final CompressionReference TASK = reference(CompressionReferenceType.TASK, "task-1", "task-state");
    private static final CompressionReference EVIDENCE = reference(CompressionReferenceType.EVIDENCE, "evidence-1",
            "snapshot-state");

    @Test
    void validCompressionPreservesReferencesAndUnknownItems() {
        ContextCompressionService service = new ContextCompressionService(CompressionConfiguration.defaults());

        var result = service.compress(TASK_ID, Set.of(TASK, EVIDENCE),
                Map.of("task-1", "config-check", "evidence-1", "approved snapshot"),
                Set.of("source pagination not confirmed"), catalog(TASK, EVIDENCE));

        assertThat(result.taskId()).isEqualTo(TASK_ID);
        assertThat(result.references()).containsExactlyInAnyOrder(TASK, EVIDENCE);
        assertThat(result.summaries()).containsEntry("evidence-1", "approved snapshot");
        assertThat(result.unknownItems()).containsExactly("source pagination not confirmed");
    }

    @Test
    void fabricatedReferenceAndDigestMismatchAreRejected() {
        ContextCompressionService service = new ContextCompressionService(CompressionConfiguration.defaults());
        CompressionReference fabricated = reference(CompressionReferenceType.EVIDENCE, "snapshot-fake", "fake");

        assertThatThrownBy(() -> service.compress(TASK_ID, Set.of(fabricated), Map.of(), Set.of(), catalog(TASK, EVIDENCE)))
                .isInstanceOf(ContextCompressionException.class)
                .hasMessageContaining("manually reduce scope");

        CompressionReference wrongDigest = reference(CompressionReferenceType.EVIDENCE, "evidence-1", "wrong");
        assertThatThrownBy(() -> service.compress(TASK_ID, Set.of(wrongDigest), Map.of(), Set.of(), catalog(TASK, EVIDENCE)))
                .isInstanceOf(ContextCompressionException.class);
    }

    @Test
    void droppedUnknownItemAndSummaryPromotionAreRejected() {
        ContextCompressionService service = new ContextCompressionService(CompressionConfiguration.defaults());

        assertThatThrownBy(() -> service.compress(TASK_ID, Set.of(TASK), Map.of(), Set.of(),
                Set.of("approval owner not confirmed"), catalog(TASK)))
                .isInstanceOf(ContextCompressionException.class)
                .hasMessageContaining("unknown items");

        assertThatThrownBy(() -> service.compress(TASK_ID, Set.of(TASK),
                Map.of("new-finding-id", "this is not evidence"), Set.of(), catalog(TASK)))
                .isInstanceOf(ContextCompressionException.class);
    }

    @Test
    void unsupportedTypeAndOverlongSummaryAreRejected() {
        CompressionConfiguration configuration = new CompressionConfiguration(10,
                Set.of(CompressionReferenceType.EVIDENCE), true);
        ContextCompressionService service = new ContextCompressionService(configuration);

        assertThatThrownBy(() -> service.compress(TASK_ID, Set.of(TASK), Map.of(), Set.of(), catalog(TASK)))
                .isInstanceOf(ContextCompressionException.class);

        assertThatThrownBy(() -> service.compress(TASK_ID, Set.of(EVIDENCE),
                Map.of("evidence-1", "summary longer than ten"), Set.of(), catalog(EVIDENCE)))
                .isInstanceOf(ContextCompressionException.class);
    }

    private static CompressionReference reference(CompressionReferenceType type, String id, String source) {
        return new CompressionReference(type, id, Sha256Digest.hex(source));
    }

    private static Map<String, CompressionReference> catalog(CompressionReference... references) {
        return Map.ofEntries(java.util.Arrays.stream(references)
                .map(reference -> Map.entry(reference.referenceId(), reference)).toArray(Map.Entry[]::new));
    }
}
