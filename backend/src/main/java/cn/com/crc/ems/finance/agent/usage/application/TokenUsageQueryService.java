package cn.com.crc.ems.finance.agent.usage.application;

import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/**
 * @Description: 生成固定 30 个自然日的 token 用量摘要；普通用户看本人，平台管理员看全平台。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 15:00:00
 */
@Service
public class TokenUsageQueryService {

    /** 统计窗口天数，含今天。 */
    static final int WINDOW_DAYS = 30;
    /** 与业务侧展示口径一致：按北京时间切分自然日。 */
    static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Shanghai");

    private final TokenUsageRepository repository;
    private final Clock clock;

    public TokenUsageQueryService(TokenUsageRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    /**
     * @description: 按当前身份生成 30 天用量摘要，缺失日期补 0。
     * @param: identity 已完成解析的当前身份
     * @return: 个人或全平台的累计总量与按天序列
     * @author: yigeyige99
     */
    public TokenUsageSummary summary(CurrentIdentity identity) {
        if (identity == null) {
            throw new IllegalArgumentException("identity is required");
        }
        boolean platformWide = identity.hasRole(RoleCode.PLATFORM_ADMIN);
        LocalDate today = LocalDate.now(clock.withZone(BUSINESS_ZONE));
        LocalDate firstDay = today.minusDays(WINDOW_DAYS - 1L);
        OffsetDateTime from = firstDay.atStartOfDay(BUSINESS_ZONE).toOffsetDateTime().withOffsetSameInstant(ZoneOffset.UTC);
        OffsetDateTime to = today.plusDays(1).atStartOfDay(BUSINESS_ZONE).toOffsetDateTime().withOffsetSameInstant(ZoneOffset.UTC);
        UUID userId = platformWide ? null : identity.userId();

        Map<LocalDate, Long> daily = new HashMap<>();
        for (TokenUsageRepository.DailyTokens row : repository.sumDaily(userId, from, to)) {
            daily.merge(row.date(), row.tokens(), Long::sum);
        }
        List<TokenUsagePoint> points = new ArrayList<>(WINDOW_DAYS);
        long total = 0;
        for (int offset = 0; offset < WINDOW_DAYS; offset++) {
            LocalDate date = firstDay.plusDays(offset);
            long tokens = daily.getOrDefault(date, 0L);
            total += tokens;
            points.add(new TokenUsagePoint(date, tokens));
        }
        return new TokenUsageSummary(platformWide ? TokenUsageSummary.SCOPE_ALL : TokenUsageSummary.SCOPE_SELF, total, points);
    }
}
