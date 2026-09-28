package cn.com.crc.ems.finance.agent.harness.application;

/** 结构化模型值及其非敏感元数据。 */
public record ModelResult<T>(T value, ModelMetadata metadata) {

    public ModelResult {
        if (value == null || metadata == null) {
            throw new IllegalArgumentException("model result value and metadata are required");
        }
    }
}
