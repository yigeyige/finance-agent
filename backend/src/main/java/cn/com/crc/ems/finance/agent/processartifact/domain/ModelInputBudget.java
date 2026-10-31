package cn.com.crc.ems.finance.agent.processartifact.domain;

import java.nio.charset.StandardCharsets;

/**
 *
 * @Description: 模型调用前的保守输入规模门禁，以 UTF-8 字节数作为 Token 数上界并预留输出空间。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 12:30:00
 */
public final class ModelInputBudget {

    private ModelInputBudget() { }

    /** 未知模型按 64K 窗口限制，已知旧模型按 128K、新模型按 1M 限制。 */
    public static void requireWithinLimit(String modelName, String instruction) {
        int limit = switch (modelName == null ? "" : modelName) {
            case "deepseek-flash", "deepseek-v4-pro" -> 1_000_000;
            case "deepseek-chat", "deepseek-reasoner" -> 128_000;
            default -> 64_000;
        };
        int inputBytes = instruction == null ? 0 : instruction.getBytes(StandardCharsets.UTF_8).length;
        if (inputBytes > limit - 16_000) {
            throw new IllegalStateException("本次模型输入超过可用上下文，请缩减材料后重试");
        }
    }
}
