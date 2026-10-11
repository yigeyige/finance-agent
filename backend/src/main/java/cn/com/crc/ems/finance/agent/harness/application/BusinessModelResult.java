package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 模型业务产出及不含提示词、推理过程的调用元数据。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record BusinessModelResult<T>(T value, BusinessModelCallMetadata metadata) {

    public BusinessModelResult {
        if (value == null || metadata == null) {
            throw new IllegalArgumentException("model value and metadata are required");
        }
    }
}
