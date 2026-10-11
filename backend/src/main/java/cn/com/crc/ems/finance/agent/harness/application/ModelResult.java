package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 结构化模型值及其非敏感元数据。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ModelResult<T>(T value, ModelMetadata metadata) {

    public ModelResult {
        if (value == null || metadata == null) {
            throw new IllegalArgumentException("model result value and metadata are required");
        }
    }
}
