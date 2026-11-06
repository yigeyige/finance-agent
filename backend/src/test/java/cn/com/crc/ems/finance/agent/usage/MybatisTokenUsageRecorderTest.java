package cn.com.crc.ems.finance.agent.usage;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.usage.infrastructure.MybatisTokenUsageRecorder;
import cn.com.crc.ems.finance.agent.usage.infrastructure.entity.ModelTokenUsageEntity;
import cn.com.crc.ems.finance.agent.usage.infrastructure.mapper.ModelTokenUsageMapper;

/**
 * @Description: 验证用量写入跳过零值，并在数据库异常时不影响调用方。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 16:10:00
 */
class MybatisTokenUsageRecorderTest {

    @Test
    void skipsZeroTokensWithoutWriting() {
        ModelTokenUsageMapper mapper = mock(ModelTokenUsageMapper.class);
        MybatisTokenUsageRecorder recorder = new MybatisTokenUsageRecorder(mapper);

        recorder.record(UUID.randomUUID(), null, "deepseek-chat", 0, Instant.now());

        verify(mapper, never()).insert(any(ModelTokenUsageEntity.class));
    }

    @Test
    void swallowsDatabaseFailure() {
        ModelTokenUsageMapper mapper = mock(ModelTokenUsageMapper.class);
        when(mapper.insert(any(ModelTokenUsageEntity.class))).thenThrow(new IllegalStateException("db down"));
        MybatisTokenUsageRecorder recorder = new MybatisTokenUsageRecorder(mapper);

        assertThatCode(() -> recorder.record(UUID.randomUUID(), UUID.randomUUID(), "deepseek-chat", 120, Instant.now()))
                .doesNotThrowAnyException();
    }
}
