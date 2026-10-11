package cn.com.crc.ems.finance.agent.task.web;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.ScopeRevision;
import cn.com.crc.ems.finance.agent.task.domain.TaskPremise;

/**
 *
 * @Description: 任务发起 API 的最小响应，避免回显原始问题和 JSON 内容。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record TaskIntakeResponse(UUID taskId, String title, String status, long rowVersion,
        UUID currentScopeRevisionId, Instant createdAt, UUID premiseId, Integer scopeRevisionNo,
        String scopeDigest) {

    public static TaskIntakeResponse task(InvestigationTask task) {
        return new TaskIntakeResponse(task.taskId(), task.title(), task.status().name(), task.rowVersion(),
                task.currentScopeRevisionId(), task.createdAt(), null, null, null);
    }

    public static TaskIntakeResponse premise(InvestigationTask task, TaskPremise premise) {
        return new TaskIntakeResponse(task.taskId(), task.title(), task.status().name(), task.rowVersion(),
                task.currentScopeRevisionId(), task.createdAt(), premise.premiseId(), null, null);
    }

    public static TaskIntakeResponse scope(InvestigationTask task, ScopeRevision revision) {
        return new TaskIntakeResponse(task.taskId(), task.title(), task.status().name(), task.rowVersion(),
                task.currentScopeRevisionId(), task.createdAt(), null, revision.revisionNo(), revision.scopeDigest());
    }
}
