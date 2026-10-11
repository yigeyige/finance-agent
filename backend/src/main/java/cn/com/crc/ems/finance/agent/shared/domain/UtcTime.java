package cn.com.crc.ems.finance.agent.shared.domain;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @Description: 统一使用 UTC Instant，并提供 API 和展示边界的时间转换。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public final class UtcTime {

    private UtcTime() {
    }

    /** 从注入时钟获取当前 UTC 时间。 */
    public static Instant now(Clock clock) {
        if (clock == null) {
            throw new IllegalArgumentException("clock is required");
        }
        return clock.instant();
    }

    /** 将时间编码为 API 使用的 ISO-8601 UTC 文本。 */
    public static String formatApi(Instant instant) {
        if (instant == null) {
            throw new IllegalArgumentException("instant is required");
        }
        return DateTimeFormatter.ISO_INSTANT.format(instant);
    }

    /** 解析 API ISO-8601 UTC 文本。 */
    public static Instant parseApi(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UTC time text is required");
        }
        return Instant.parse(value);
    }

    /** 将 UTC 时间转换为指定展示时区。 */
    public static ZonedDateTime inZone(Instant instant, ZoneId zoneId) {
        if (instant == null || zoneId == null) {
            throw new IllegalArgumentException("instant and zoneId are required");
        }
        return instant.atZone(zoneId);
    }
}
