package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundle;
import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundleStatus;
import cn.com.crc.ems.finance.agent.harness.application.AllowedContextFactory;
import cn.com.crc.ems.finance.agent.harness.application.ContextEvidence;
import cn.com.crc.ems.finance.agent.harness.application.ModelPurpose;
import cn.com.crc.ems.finance.agent.harness.application.PromptTemplateCatalog;
import cn.com.crc.ems.finance.agent.harness.application.PromptTemplateDescriptor;
import cn.com.crc.ems.finance.agent.harness.application.PromptTemplateStatus;
import cn.com.crc.ems.finance.agent.harness.application.PromptTemplateVersion;

class AllowedContextFactoryTest {

    private static final UUID TASK = UUID.randomUUID();
    private static final String DIGEST = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    @Test
    void buildsContextOnlyFromCurrentTaskEvidenceAndFieldWhitelist() {
        var evidence = new ContextEvidence("evidence-1", TASK,
                JsonNodeFactory.instance.objectNode().put("status", "CURRENT"), Set.of("status"));
        var context = AllowedContextFactory.create(TASK, List.of(evidence), Map.of("taskType", "config-check"),
                Set.of("status"));

        assertThat(context.evidenceIds()).containsExactly("evidence-1");
        assertThat(context.allowedFields()).containsExactly("status");
        assertThat(context.fieldDigest()).hasSize(64);
    }

    @Test
    void rejectsCrossTaskOrSensitiveEvidence() {
        var otherTask = new ContextEvidence("evidence-2", UUID.randomUUID(),
                JsonNodeFactory.instance.objectNode().put("status", "CURRENT"), Set.of("status"));
        assertThatThrownBy(() -> AllowedContextFactory.create(TASK, List.of(otherTask), Map.of(), Set.of("status")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ContextEvidence("evidence-3", TASK,
                JsonNodeFactory.instance.objectNode().put("reasoning", "secret"), Set.of("reasoning")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void requiresPublishedTemplateBoundToPublishedRuntimeBundle() {
        PromptTemplateVersion version = new PromptTemplateVersion("step-proposal", "prompt-v1");
        PromptTemplateCatalog catalog = new PromptTemplateCatalog(List.of(new PromptTemplateDescriptor(version,
                ModelPurpose.STEP_PROPOSAL, DIGEST, PromptTemplateStatus.PUBLISHED)));
        RuntimeBundle bundle = new RuntimeBundle(UUID.randomUUID(), "app", "2.0.3", "model-v1", "prompt-v1",
                "rules-v1", "tools-v1", "sources-v1", null, DIGEST, RuntimeBundleStatus.PUBLISHED,
                Instant.parse("2026-09-28T00:00:00Z"), UUID.randomUUID(), Instant.parse("2026-09-28T00:00:00Z"),
                UUID.randomUUID());

        assertThat(catalog.requirePublished(bundle, ModelPurpose.STEP_PROPOSAL, version)).isNotNull();
        RuntimeBundle draft = new RuntimeBundle(bundle.runtimeBundleId(), bundle.applicationVersion(),
                bundle.agentscopeVersion(), bundle.modelBindingVersion(), bundle.promptBundleVersion(),
                bundle.ruleSetVersion(), bundle.toolCatalogVersion(), bundle.sourceCatalogVersion(), null,
                bundle.bundleDigest(), RuntimeBundleStatus.DRAFT, bundle.createdAt(), bundle.createdBy(),
                bundle.updatedAt(), bundle.updatedBy());
        assertThatThrownBy(() -> catalog.requirePublished(draft, ModelPurpose.STEP_PROPOSAL, version))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
