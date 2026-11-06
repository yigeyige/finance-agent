package cn.com.crc.ems.finance.agent.usage.application;

import java.time.LocalDate;

/**
 * @Description: 单个自然日的 token 用量，日期按业务时区（Asia/Shanghai）切分。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 15:00:00
 */
public record TokenUsagePoint(LocalDate date, long tokens) {
}
