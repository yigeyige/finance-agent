package cn.com.crc.ems.finance.agent.usage.infrastructure;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import cn.com.crc.ems.finance.agent.usage.application.TokenUsageRepository;
import cn.com.crc.ems.finance.agent.usage.infrastructure.mapper.ModelTokenUsageDailyRow;
import cn.com.crc.ems.finance.agent.usage.infrastructure.mapper.ModelTokenUsageMapper;

/**
 * @Description: 基于 MyBatis-Plus 的 token 用量聚合查询实现。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 15:00:00
 */
@Repository
public class MybatisTokenUsageRepository implements TokenUsageRepository {

    private final ModelTokenUsageMapper mapper;

    public MybatisTokenUsageRepository(ModelTokenUsageMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<DailyTokens> sumDaily(UUID userId, OffsetDateTime from, OffsetDateTime to) {
        if (from == null || to == null || !from.isBefore(to)) {
            throw new IllegalArgumentException("usage time range is invalid");
        }
        List<ModelTokenUsageDailyRow> rows = userId == null
                ? mapper.sumDailyAll(from, to)
                : mapper.sumDailyByUser(userId, from, to);
        return rows.stream()
                .map(row -> new DailyTokens(row.getUsageDate(), row.getTotalTokens() == null ? 0 : row.getTotalTokens()))
                .toList();
    }
}
