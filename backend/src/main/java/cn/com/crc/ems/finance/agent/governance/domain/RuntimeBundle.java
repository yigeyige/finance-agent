package cn.com.crc.ems.finance.agent.governance.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @Description: 描述一次可追溯、可发布的 Agent 运行版本组合。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record RuntimeBundle(
        UUID runtimeBundleId,
        String applicationVersion,
        String agentscopeVersion,
        String modelBindingVersion,
        String promptBundleVersion,
        String ruleSetVersion,
        String toolCatalogVersion,
        String sourceCatalogVersion,
        UUID knowledgeReleaseId,
        String bundleDigest,
        RuntimeBundleStatus status,
        Instant createdAt,
        UUID createdBy,
        Instant updatedAt,
        UUID updatedBy) {

    public RuntimeBundle {
        Objects.requireNonNull(runtimeBundleId, "runtimeBundleId");
        requireText(applicationVersion, "applicationVersion");
        requireText(agentscopeVersion, "agentscopeVersion");
        requireText(modelBindingVersion, "modelBindingVersion");
        requireText(promptBundleVersion, "promptBundleVersion");
        requireText(ruleSetVersion, "ruleSetVersion");
        requireText(toolCatalogVersion, "toolCatalogVersion");
        requireText(sourceCatalogVersion, "sourceCatalogVersion");
        requireText(bundleDigest, "bundleDigest");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(createdAt, "createdAt");
        Objects.requireNonNull(updatedAt, "updatedAt");
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
