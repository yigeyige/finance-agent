package cn.com.crc.ems.finance.agent.harness.application;

import java.util.Objects;

/**
 *
 * @Description: 模型调用提示词模板的受控正文，不含运行时输入。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-23 18:00:00
 */
public record PromptTemplate(String templateCode, String content) {

    /** 校验模板编码与正文均非空，阻止不完整模板进入模型调用。 */
    public PromptTemplate {
        if (templateCode == null || templateCode.isBlank() || templateCode.length() > 100) {
            throw new IllegalArgumentException("templateCode is invalid");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content is invalid");
        }
        Objects.requireNonNull(content, "content is required");
    }
}
