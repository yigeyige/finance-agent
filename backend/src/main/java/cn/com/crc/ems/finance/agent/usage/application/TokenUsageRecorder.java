package cn.com.crc.ems.finance.agent.usage.application;

import java.time.Instant;
import java.util.UUID;

/**
 * @Description: 模型 token 用量写入端口，隔离 harness 与持久化实现。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 14:10:00
 */
public interface TokenUsageRecorder {

    /**
     * @description: 记录一次真实模型调用的 token 用量；实现必须吞掉写入异常，不得影响调用方。
     * @param: userId 归属用户
     * @param: taskId 关联任务，可为空
     * @param: modelName 实际调用的模型名
     * @param: tokens 本次调用消耗的 token，必须大于 0
     * @param: occurredAt 调用完成时间
     * @author: yigeyige99
     */
    void record(UUID userId, UUID taskId, String modelName, int tokens, Instant occurredAt);
}
