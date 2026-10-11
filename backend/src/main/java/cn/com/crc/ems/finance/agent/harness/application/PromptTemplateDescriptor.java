package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 不保存模板原文、只保存用途和内容摘要的模板目录条目。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record PromptTemplateDescriptor(PromptTemplateVersion version, ModelPurpose purpose,
        String templateDigest, PromptTemplateStatus status) {

    public PromptTemplateDescriptor {
        if (version == null || purpose == null || templateDigest == null
                || !templateDigest.matches("[0-9a-fA-F]{64}") || status == null) {
            throw new IllegalArgumentException("prompt template descriptor is invalid");
        }
    }
}
