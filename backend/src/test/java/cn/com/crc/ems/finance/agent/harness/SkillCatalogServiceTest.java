package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.harness.application.SkillCatalogService;
import cn.com.crc.ems.finance.agent.harness.application.SkillPackage;
import cn.com.crc.ems.finance.agent.harness.application.SkillStatus;
import cn.com.crc.ems.finance.agent.harness.application.SyntheticInvestigationSkill;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.tool.application.ToolCatalog;
import cn.com.crc.ems.finance.agent.tool.application.ToolDescriptor;

class SkillCatalogServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-29T02:20:00Z");

    @Test
    void onlyPublishedDigestMatchingSkillsLoadAndTaskVersionCannotDrift() {
        UUID skillId = UUID.randomUUID();
        UUID tester = UUID.randomUUID();
        UUID approver = UUID.randomUUID();
        SkillPackage draft = skill(skillId, Set.of("read-config"), Set.of("SAFE"));
        SkillCatalogService service = service();
        service.register(draft);

        assertThatThrownBy(() -> service.loadPublished(skillId, "v1", draft.contentDigest()))
                .isInstanceOf(IllegalArgumentException.class);
        service.markTested(skillId, "v1", draft.testEvidenceDigest(), tester);
        SkillPackage published = service.publish(skillId, "v1", approver);

        assertThat(service.loadPublished(skillId, "v1", published.contentDigest())).isEqualTo(published);
        assertThatThrownBy(() -> service.loadPublished(skillId, "v1", Sha256Digest.hex("tampered")))
                .isInstanceOf(IllegalArgumentException.class);

        UUID taskId = UUID.randomUUID();
        UUID bundleId = UUID.randomUUID();
        var frozen = service.freezeForTask(taskId, bundleId, skillId, "v1", published.contentDigest());
        assertThat(service.freezeForTask(taskId, bundleId, skillId, "v1", published.contentDigest())).isEqualTo(frozen);
        assertThatThrownBy(() -> service.freezeForTask(taskId, UUID.randomUUID(), skillId, "v1",
                published.contentDigest())).isInstanceOf(IllegalStateException.class);

        service.retire(skillId, "v1", approver);
        assertThatThrownBy(() -> service.loadPublished(skillId, "v1", published.contentDigest()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(service.approvalEvents()).hasSize(3);
    }

    @Test
    void rejectsPermissionSupersetAndTamperedContent() {
        SkillCatalogService service = service();
        SkillPackage unknownTool = skill(UUID.randomUUID(), Set.of("not-in-catalog"), Set.of("SAFE"));
        assertThatThrownBy(() -> service.register(unknownTool)).isInstanceOf(IllegalArgumentException.class);

        String content = "synthetic-skill";
        assertThatThrownBy(() -> new SkillPackage(UUID.randomUUID(), "tampered", "v1", Set.of("investigation"),
                "input-v1", "output-v1", content, Sha256Digest.hex("other"), Sha256Digest.hex("test"),
                Set.of(), Set.of("SAFE"), SkillStatus.DRAFT, null, NOW, NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void syntheticSkillIsDeterministicAndDoesNotRequireDataAccess() {
        SyntheticInvestigationSkill skill = new SyntheticInvestigationSkill();
        var first = skill.execute(SyntheticInvestigationSkill.SCENARIO);
        var second = skill.execute(SyntheticInvestigationSkill.SCENARIO);

        assertThat(second).isEqualTo(first);
        assertThat(first.evidenceRefs()).allMatch(value -> value.startsWith("synthetic://"));
        assertThatThrownBy(() -> skill.execute("production-data")).isInstanceOf(IllegalArgumentException.class);
    }

    private SkillCatalogService service() {
        ToolDescriptor descriptor = new ToolDescriptor("read-config", "v1", "input-v1", "output-v1", "LOW",
                Set.of(TaskStatus.ANALYZING), false, 1000, 1024, Set.of("test"), false);
        ToolCatalog catalog = new ToolCatalog("tool-catalog-v1", Sha256Digest.hex("catalog"), List.of(descriptor));
        return new SkillCatalogService(catalog, Set.of("SAFE"), Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private SkillPackage skill(UUID skillId, Set<String> tools, Set<String> fields) {
        String content = "synthetic-skill-v1";
        return new SkillPackage(skillId, "synthetic-investigation", "v1", Set.of("investigation"), "input-v1",
                "output-v1", content, Sha256Digest.hex(content), Sha256Digest.hex("test-evidence"), tools, fields,
                SkillStatus.DRAFT, null, NOW, NOW);
    }
}
