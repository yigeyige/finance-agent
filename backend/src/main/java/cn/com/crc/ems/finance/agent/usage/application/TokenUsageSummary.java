package cn.com.crc.ems.finance.agent.usage.application;

import java.util.List;

/**
 * @Description: 固定 30 天的 token 用量摘要；scope 表示个人（SELF）或全平台（ALL）。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 15:00:00
 */
public record TokenUsageSummary(String scope, long totalTokens, List<TokenUsagePoint> points) {

    public static final String SCOPE_SELF = "SELF";
    public static final String SCOPE_ALL = "ALL";

    public TokenUsageSummary {
        if (!SCOPE_SELF.equals(scope) && !SCOPE_ALL.equals(scope)) {
            throw new IllegalArgumentException("usage scope is invalid");
        }
        if (totalTokens < 0 || points == null) {
            throw new IllegalArgumentException("usage summary is invalid");
        }
        points = List.copyOf(points);
    }
}
