package cn.com.crc.ems.finance.agent.diagnostics;

import java.util.Arrays;
import java.util.regex.Pattern;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

import org.slf4j.Marker;

/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:31
 */
public final class DiagnosticLogFilter extends TurboFilter {

    private static final Pattern FORBIDDEN = Pattern.compile(
            "(?i)(password\\s*[=:]|api[-_]?key\\s*[=:]|authorization\\s*[:=]|bearer\\s+|"
                    + "prompt\\s*[=:]|response\\s*[=:]|reasoning\\s*[=:]|sk-[a-z0-9_-]{8,})");

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format,
                               Object[] params, Throwable throwable) {
        StringBuilder candidate = new StringBuilder(format == null ? "" : format);
        if (params != null) {
            Arrays.stream(params).forEach(value -> candidate.append(' ').append(value));
        }
        if (throwable != null) {
            candidate.append(' ').append(throwable.getMessage());
        }
        return FORBIDDEN.matcher(candidate).find() ? FilterReply.DENY : FilterReply.NEUTRAL;
    }
}
