package cn.com.crc.ems.finance.agent.task.application;

import java.util.List;

import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;

/**
 *
 * @Description: 身份范围内任务分页查询的不可变结果。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record TaskPage(List<InvestigationTask> items, long total, int page, int pageSize) {

    public TaskPage {
        items = List.copyOf(items);
    }
}
