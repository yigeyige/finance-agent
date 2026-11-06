package cn.com.crc.ems.finance.agent.usage;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.usage.application.TokenUsageQueryService;
import cn.com.crc.ems.finance.agent.usage.application.TokenUsageRepository;
import cn.com.crc.ems.finance.agent.usage.application.TokenUsageSummary;

/**
 * @Description: 验证 30 天窗口、缺失日期补 0、个人/全平台口径与北京时间边界。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 16:00:00
 */
class TokenUsageQueryServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-06T12:00:00Z");

    @Test
    void fillsThirtyDaysAndSumsOnlyRowsInsideWindow() {
        CapturingRepository repository = new CapturingRepository(List.of(
                new TokenUsageRepository.DailyTokens(LocalDate.parse("2026-10-07"), 999),
                new TokenUsageRepository.DailyTokens(LocalDate.parse("2026-10-08"), 50),
                new TokenUsageRepository.DailyTokens(LocalDate.parse("2026-11-06"), 100)));
        TokenUsageQueryService service = new TokenUsageQueryService(repository, fixedClock());
        UUID userId = UUID.randomUUID();

        TokenUsageSummary summary = service.summary(identity(userId, RoleCode.PLATFORM_USER));

        assertThat(summary.scope()).isEqualTo(TokenUsageSummary.SCOPE_SELF);
        assertThat(summary.points()).hasSize(30);
        assertThat(summary.points().get(0).date()).isEqualTo(LocalDate.parse("2026-10-08"));
        assertThat(summary.points().get(0).tokens()).isEqualTo(50);
        assertThat(summary.points().get(29).date()).isEqualTo(LocalDate.parse("2026-11-06"));
        assertThat(summary.points().get(29).tokens()).isEqualTo(100);
        assertThat(summary.totalTokens()).isEqualTo(150);
        assertThat(repository.lastUserId).isEqualTo(userId);
    }

    @Test
    void administratorReadsPlatformWideUsage() {
        CapturingRepository repository = new CapturingRepository(List.of());
        TokenUsageQueryService service = new TokenUsageQueryService(repository, fixedClock());

        TokenUsageSummary summary = service.summary(identity(UUID.randomUUID(), RoleCode.PLATFORM_ADMIN));

        assertThat(summary.scope()).isEqualTo(TokenUsageSummary.SCOPE_ALL);
        assertThat(summary.totalTokens()).isZero();
        assertThat(summary.points()).allMatch(point -> point.tokens() == 0);
        assertThat(repository.lastUserId).isNull();
    }

    @Test
    void windowStartsAtBeijingMidnight() {
        CapturingRepository repository = new CapturingRepository(List.of());
        TokenUsageQueryService service = new TokenUsageQueryService(repository, fixedClock());

        service.summary(identity(UUID.randomUUID(), RoleCode.PLATFORM_USER));

        // 北京时间 2026-10-08 00:00 == UTC 2026-10-07 16:00；结束为北京时间 11-07 00:00。
        assertThat(repository.lastFrom.toInstant()).isEqualTo(Instant.parse("2026-10-07T16:00:00Z"));
        assertThat(repository.lastTo.toInstant()).isEqualTo(Instant.parse("2026-11-06T16:00:00Z"));
    }

    private static Clock fixedClock() {
        return Clock.fixed(NOW, ZoneOffset.UTC);
    }

    private static CurrentIdentity identity(UUID userId, RoleCode role) {
        return new CurrentIdentity(userId, "subject-" + userId, "测试用户", Set.of(role));
    }

    /** 记录查询入参并返回预设聚合结果。 */
    private static final class CapturingRepository implements TokenUsageRepository {
        private final List<DailyTokens> rows;
        private UUID lastUserId;
        private OffsetDateTime lastFrom;
        private OffsetDateTime lastTo;

        private CapturingRepository(List<DailyTokens> rows) {
            this.rows = new ArrayList<>(rows);
        }

        @Override
        public List<DailyTokens> sumDaily(UUID userId, OffsetDateTime from, OffsetDateTime to) {
            this.lastUserId = userId;
            this.lastFrom = from;
            this.lastTo = to;
            return rows;
        }
    }
}
