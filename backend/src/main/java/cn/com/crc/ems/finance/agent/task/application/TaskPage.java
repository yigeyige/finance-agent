package cn.com.crc.ems.finance.agent.task.application;

import java.util.List;

import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;

/** 身份范围内任务分页查询的不可变结果。 */
public record TaskPage(List<InvestigationTask> items, long total, int page, int pageSize) {

    public TaskPage {
        items = List.copyOf(items);
    }
}
