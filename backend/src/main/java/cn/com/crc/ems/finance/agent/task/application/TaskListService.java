package cn.com.crc.ems.finance.agent.task.application;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/** 为工作台提供当前发起人的只读任务清单。 */
@Service
public class TaskListService {
    private final InvestigationTaskRepository taskRepository;
    public TaskListService(InvestigationTaskRepository taskRepository) { this.taskRepository = taskRepository; }
    /** 查询当前身份可见的任务页；平台管理员可查看全部任务。 */
    public TaskPage list(CurrentIdentity identity, int page, int pageSize, TaskStatus status, String keyword) {
        return identity.hasRole(RoleCode.PLATFORM_ADMIN)
                ? taskRepository.findPage(page, pageSize, status, normalizeKeyword(keyword))
                : taskRepository.findPageByInitiator(identity.userId(), page, pageSize, status, normalizeKeyword(keyword));
    }

    private static String normalizeKeyword(String keyword) {
        if (keyword == null) return null;
        String normalized = keyword.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
