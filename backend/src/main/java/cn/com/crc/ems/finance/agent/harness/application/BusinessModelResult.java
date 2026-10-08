package cn.com.crc.ems.finance.agent.harness.application;

/** 模型业务产出及不含提示词、推理过程的调用元数据。 */
public record BusinessModelResult<T>(T value, BusinessModelCallMetadata metadata) {

    public BusinessModelResult {
        if (value == null || metadata == null) {
            throw new IllegalArgumentException("model value and metadata are required");
        }
    }
}
