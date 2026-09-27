package cn.com.crc.ems.finance.agent.task.application;

import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.ScopeRevision;

/** 范围确认后的任务和不可变修订快照。 */
public record ScopeConfirmationResult(InvestigationTask task, ScopeRevision scopeRevision) {

    public ScopeConfirmationResult {
        if (task == null || scopeRevision == null) {
            throw new IllegalArgumentException("task and scopeRevision are required");
        }
    }
}
