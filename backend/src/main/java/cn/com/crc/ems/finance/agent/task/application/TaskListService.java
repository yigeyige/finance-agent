package cn.com.crc.ems.finance.agent.task.application;

import java.util.List;
import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;

/** 为工作台提供当前发起人的只读任务清单。 */
@Service
public class TaskListService {
    private final InvestigationTaskRepository taskRepository;
    public TaskListService(InvestigationTaskRepository taskRepository) { this.taskRepository = taskRepository; }
    /** 查询当前身份可见的最近任务；平台管理员可查看全部任务。 */
    public List<InvestigationTask> listRecent(CurrentIdentity identity) {
        return identity.hasRole(RoleCode.PLATFORM_ADMIN) ? taskRepository.findRecent()
                : taskRepository.findRecentByInitiator(identity.userId());
    }
}
