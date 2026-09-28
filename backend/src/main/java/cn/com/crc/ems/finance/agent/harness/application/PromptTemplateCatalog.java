package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundle;
import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundleStatus;

/** 版本化提示模板目录，只允许 runtime bundle 引用已发布模板。 */
@Component
public final class PromptTemplateCatalog {

    private final List<PromptTemplateDescriptor> descriptors;

    public PromptTemplateCatalog() {
        this(List.of());
    }

    public PromptTemplateCatalog(List<PromptTemplateDescriptor> descriptors) {
        this.descriptors = descriptors == null ? List.of() : List.copyOf(descriptors);
    }

    /** 校验模板属于当前已发布 runtime bundle，返回目录条目而不暴露模板原文。 */
    public PromptTemplateDescriptor requirePublished(RuntimeBundle bundle,
            ModelPurpose purpose, PromptTemplateVersion version) {
        Objects.requireNonNull(bundle, "runtime bundle is required");
        Objects.requireNonNull(purpose, "purpose is required");
        Objects.requireNonNull(version, "version is required");
        if (bundle.status() != RuntimeBundleStatus.PUBLISHED
                || !bundle.promptBundleVersion().equals(version.version())) {
            throw new IllegalArgumentException("prompt template is not bound to published runtime bundle");
        }
        return descriptors.stream().filter(item -> item.purpose() == purpose && item.version().equals(version)
                && item.status() == PromptTemplateStatus.PUBLISHED).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("prompt template is not published"));
    }

    public List<PromptTemplateDescriptor> entries() {
        return descriptors;
    }
}
