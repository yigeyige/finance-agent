package cn.com.crc.ems.finance.agent.task.application;

import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.ScopeRevision;

/**
 *
 * @Description: 范围确认后的任务和不可变修订快照。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record ScopeConfirmationResult(InvestigationTask task, ScopeRevision scopeRevision) {

    public ScopeConfirmationResult {
        if (task == null || scopeRevision == null) {
            throw new IllegalArgumentException("task and scopeRevision are required");
        }
    }
}
