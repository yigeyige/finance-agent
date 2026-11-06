package cn.com.crc.ems.finance.agent.usage.web;

import java.util.List;

import cn.com.crc.ems.finance.agent.usage.application.TokenUsagePoint;
import cn.com.crc.ems.finance.agent.usage.application.TokenUsageSummary;

/**
 * @Description: token 用量接口响应：scope 区分个人/全平台，points 固定 30 个自然日。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 15:10:00
 */
public record TokenUsageResponse(String scope, long totalTokens, List<Point> points) {

    public TokenUsageResponse {
        points = points == null ? List.of() : List.copyOf(points);
    }

    /** 转换应用层摘要为对外响应。 */
    public static TokenUsageResponse from(TokenUsageSummary summary) {
        return new TokenUsageResponse(summary.scope(), summary.totalTokens(),
                summary.points().stream().map(point -> new Point(point.date().toString(), point.tokens())).toList());
    }

    /** 单个自然日的 token 用量。 */
    public record Point(String date, long tokens) {
    }
}
