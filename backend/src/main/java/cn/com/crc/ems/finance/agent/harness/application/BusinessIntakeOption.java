package cn.com.crc.ems.finance.agent.harness.application;

/**
 * @Description: 预填解析时提供给模型的候选选项（业务场景或数据库实例）。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 15:00:00
 */
public record BusinessIntakeOption(String id, String name) {

    public BusinessIntakeOption {
        if (id == null || id.isBlank() || name == null || name.isBlank()) {
            throw new IllegalArgumentException("business intake option is invalid");
        }
    }
}
