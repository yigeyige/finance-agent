package cn.com.crc.ems.finance.agent.usage.infrastructure;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.crc.ems.finance.agent.usage.application.TokenUsageRecorder;
import cn.com.crc.ems.finance.agent.usage.infrastructure.entity.ModelTokenUsageEntity;
import cn.com.crc.ems.finance.agent.usage.infrastructure.mapper.ModelTokenUsageMapper;

/**
 * @Description: 将真实模型调用用量写入 usage.model_token_usage；写入失败只记日志，不影响模型调用。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 14:10:00
 */
@Component
public class MybatisTokenUsageRecorder implements TokenUsageRecorder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisTokenUsageRecorder.class);
    private static final String LIVE_MODE = "LIVE";

    private final ModelTokenUsageMapper mapper;

    public MybatisTokenUsageRecorder(ModelTokenUsageMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void record(UUID userId, UUID taskId, String modelName, int tokens, Instant occurredAt) {
        if (userId == null || tokens <= 0 || modelName == null || modelName.isBlank() || occurredAt == null) {
            return;
        }
        try {
            ModelTokenUsageEntity entity = new ModelTokenUsageEntity();
            entity.userId = userId;
            entity.taskId = taskId;
            entity.modelName = modelName;
            entity.mode = LIVE_MODE;
            entity.tokens = (long) tokens;
            entity.occurredAt = occurredAt;
            mapper.insert(entity);
        } catch (RuntimeException exception) {
            // 用量属于旁路统计，任何写入失败都不能影响模型调用结果。
            LOGGER.warn("模型 token 用量写入失败，已跳过本次记录: {}", exception.getClass().getSimpleName());
        }
    }
}
