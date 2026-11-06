package cn.com.crc.ems.finance.agent.usage.infrastructure.mapper;

import java.time.LocalDate;

/**
 * @Description: 按天聚合查询的 MyBatis 结果行。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 15:00:00
 */
public class ModelTokenUsageDailyRow {

    private LocalDate usageDate;
    private Long totalTokens;

    public LocalDate getUsageDate() {
        return usageDate;
    }

    public void setUsageDate(LocalDate usageDate) {
        this.usageDate = usageDate;
    }

    public Long getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(Long totalTokens) {
        this.totalTokens = totalTokens;
    }
}
