package cn.com.crc.ems.finance.agent.task.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.businesspacket.application.BusinessDecisionPacketRepository;
import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;

/**
 * @Description: 在任务权限范围内查询决策包流程产物入口是否具备场景提示词。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-02 22:10:00
 */
@Service
public class TaskProcessArtifactAvailabilityService {
    private final InvestigationTaskRepository tasks;
    private final TaskBusinessSceneSkillRepository associations;
    private final BusinessSceneSkillRepository skills;
    private final BusinessDecisionPacketRepository packets;

    public TaskProcessArtifactAvailabilityService(InvestigationTaskRepository tasks,
            TaskBusinessSceneSkillRepository associations, BusinessSceneSkillRepository skills,
            BusinessDecisionPacketRepository packets) {
        this.tasks = tasks;
        this.associations = associations;
        this.skills = skills;
        this.packets = packets;
    }

    /**
     * @description: 先校验任务可见性，再按关联场景检查非空白流程文件提示词。
     * @param: taskId 任务标识
     * @param: identity 当前可信身份
     * @return: 不包含提示词正文的入口可用状态
     * @throws: RepositoryEntityNotFoundException 任务不存在或当前身份无权访问
     * @author: yigeyige99
     */
    @Transactional(readOnly = true)
    public Availability find(UUID taskId, CurrentIdentity identity) {
        var task = tasks.findById(taskId)
                .orElseThrow(() -> new RepositoryEntityNotFoundException("业务处理任务不存在或无权查看"));
        if (!identity.hasRole(RoleCode.PLATFORM_ADMIN) && !task.initiatorUserId().equals(identity.userId())) {
            throw new RepositoryEntityNotFoundException("业务处理任务不存在或无权查看");
        }
        if (packets.findLatestByTaskId(taskId).isEmpty()) {
            return new Availability(false);
        }
        boolean available = associations.findSkillIdByTaskId(taskId)
                .flatMap(skills::findActiveById)
                .map(skill -> skill.processArtifactPrompt() != null && !skill.processArtifactPrompt().isBlank())
                .orElse(false);
        return new Availability(available);
    }

    public record Availability(boolean available) { }
}
