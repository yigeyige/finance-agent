package cn.com.crc.ems.finance.agent.harness.application;

import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;

/**
 * @Description: 可选模型调用（选表、查询规划）的业务值与调用元数据，供统一 token 用量统计消费。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 14:00:00
 */
public record BusinessModelCall<T>(T value, BusinessModelCallMetadata metadata) {

    /** 未发起真实调用时使用的哨兵模式，统计侧只认可 LIVE，因此不会计入用量。 */
    private static final String NOT_CALLED_MODE = "STUB";
    private static final String NOT_CALLED_MODEL = "not-called";
    private static final String EMPTY_DIGEST = Sha256Digest.hex("");

    public BusinessModelCall {
        if (value == null || metadata == null) {
            throw new IllegalArgumentException("model call value and metadata are required");
        }
    }

    /**
     * @description: 构造“没有真实调用”的结果，供接口默认实现和 Stub 模式使用。
     * @param: value 兜底业务值
     * @return: token 为 0 的模型调用结果
     * @author: yigeyige99
     */
    public static <T> BusinessModelCall<T> notCalled(T value) {
        return new BusinessModelCall<>(value, new BusinessModelCallMetadata(NOT_CALLED_MODE, NOT_CALLED_MODEL,
                EMPTY_DIGEST, EMPTY_DIGEST, 0, 0));
    }

    /**
     * @description: 构造真实调用失败或缺少 usage 的结果，token 为 0，不参与统计。
     * @param: value 失败时的兜底业务值
     * @param: modelName 实际调用的模型名
     * @param: latencyMillis 已耗时毫秒
     * @return: token 为 0 的 LIVE 调用结果
     * @author: yigeyige99
     */
    public static <T> BusinessModelCall<T> failed(T value, String modelName, long latencyMillis) {
        if (value == null) {
            throw new IllegalArgumentException("model call fallback value is required");
        }
        String name = modelName == null || modelName.isBlank() ? "unknown-model" : modelName;
        return new BusinessModelCall<>(value, new BusinessModelCallMetadata("LIVE", name,
                EMPTY_DIGEST, EMPTY_DIGEST, 0, Math.max(0, latencyMillis)));
    }
}
