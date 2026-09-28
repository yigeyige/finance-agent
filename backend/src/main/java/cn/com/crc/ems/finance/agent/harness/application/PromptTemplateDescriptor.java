package cn.com.crc.ems.finance.agent.harness.application;

/** 不保存模板原文、只保存用途和内容摘要的模板目录条目。 */
public record PromptTemplateDescriptor(PromptTemplateVersion version, ModelPurpose purpose,
        String templateDigest, PromptTemplateStatus status) {

    public PromptTemplateDescriptor {
        if (version == null || purpose == null || templateDigest == null
                || !templateDigest.matches("[0-9a-fA-F]{64}") || status == null) {
            throw new IllegalArgumentException("prompt template descriptor is invalid");
        }
    }
}
