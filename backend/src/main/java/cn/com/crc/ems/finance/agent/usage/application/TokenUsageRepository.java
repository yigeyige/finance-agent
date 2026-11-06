package cn.com.crc.ems.finance.agent.usage.application;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @Description: token 用量按天聚合查询端口；userId 为空表示查询全平台。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 15:00:00
 */
public interface TokenUsageRepository {

    /**
     * @description: 查询指定时间区间内按自然日聚合的 LIVE 调用 token 用量。
     * @param: userId 归属用户，为空时聚合全平台
     * @param: from 起始时间（含）
     * @param: to 结束时间（不含）
     * @return: 有数据的日期与 token 合计，未出现的日期不在结果中
     * @author: yigeyige99
     */
    List<DailyTokens> sumDaily(UUID userId, OffsetDateTime from, OffsetDateTime to);

    /** 单个自然日的 token 合计。 */
    record DailyTokens(LocalDate date, long tokens) {
        public DailyTokens {
            if (date == null || tokens < 0) {
                throw new IllegalArgumentException("daily token usage is invalid");
            }
        }
    }
}
