package cn.com.crc.ems.finance.agent.processartifact.domain;

import java.nio.charset.StandardCharsets;

/**
 *
 * @Description: 模型调用前的保守输入规模门禁，以 UTF-8 字节数作为 Token 数上界。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 22:30:00
 */
public final class ModelInputBudget {

    private ModelInputBudget() { }

    /**
     * @description: 按模型名检查本地输入字节门禁；当前模型取 1M 输入上限的一半，输出上限独立。
     * @param: modelName 当前调用的模型名称
     * @param: instruction 即将提交给模型的完整输入
     * @throws: IllegalStateException 输入超过该模型的本地门禁
     * @author: yigeyige99
     */
    public static void requireWithinLimit(String modelName, String instruction) {
        int maxInputBytes = switch (modelName == null ? "" : modelName) {
            case "deepseek-v4.1-flash" -> 500_000;
            case "deepseek-flash", "deepseek-v4-pro" -> 984_000;
            case "deepseek-chat", "deepseek-reasoner" -> 112_000;
            default -> 48_000;
        };
        int inputBytes = instruction == null ? 0 : instruction.getBytes(StandardCharsets.UTF_8).length;
        if (inputBytes > maxInputBytes) {
            throw new IllegalStateException("本次模型输入超过可用上下文，请缩减材料后重试");
        }
    }
}
