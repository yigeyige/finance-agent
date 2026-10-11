package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 不可变的提示模板标识，不承载模板原文。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record PromptTemplateVersion(String templateCode, String version) {

    public PromptTemplateVersion {
        requireText(templateCode, "templateCode");
        requireText(version, "version");
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank() || value.length() > 100) {
            throw new IllegalArgumentException(field + " must be non-blank and at most 100 characters");
        }
    }
}
