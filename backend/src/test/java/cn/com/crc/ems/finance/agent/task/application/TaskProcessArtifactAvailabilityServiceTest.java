package cn.com.crc.ems.finance.agent.task.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.businesspacket.application.BusinessDecisionPacketRepository;
import cn.com.crc.ems.finance.agent.businesspacket.domain.BusinessDecisionPacket;
import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.scenario.domain.BusinessSceneSkill;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/**
 * @Description: 验证流程产物入口状态依据任务场景提示词和任务可见性判定。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-02 22:10:00
 */
class TaskProcessArtifactAvailabilityServiceTest {
    private final InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
    private final TaskBusinessSceneSkillRepository associations = mock(TaskBusinessSceneSkillRepository.class);
    private final BusinessSceneSkillRepository skills = mock(BusinessSceneSkillRepository.class);
    private final BusinessDecisionPacketRepository packets = mock(BusinessDecisionPacketRepository.class);
    private final TaskProcessArtifactAvailabilityService service =
            new TaskProcessArtifactAvailabilityService(tasks, associations, skills, packets);

    @Test
    void reportsConfiguredPromptForAssociatedScene() {
        UUID userId = UUID.randomUUID();
        InvestigationTask task = task(userId);
        UUID skillId = UUID.randomUUID();
        when(tasks.findById(task.taskId())).thenReturn(Optional.of(task));
        when(packets.findLatestByTaskId(task.taskId())).thenReturn(Optional.of(mock(BusinessDecisionPacket.class)));
        when(associations.findSkillIdByTaskId(task.taskId())).thenReturn(Optional.of(skillId));
        when(skills.findActiveById(skillId)).thenReturn(Optional.of(skill(skillId, "生成流程文件")));

        assertThat(service.find(task.taskId(), identity(userId)).available()).isTrue();
    }

    @Test
    void hidesEntryForEmptyPromptAndMissingScene() {
        UUID userId = UUID.randomUUID();
        InvestigationTask task = task(userId);
        UUID skillId = UUID.randomUUID();
        when(tasks.findById(task.taskId())).thenReturn(Optional.of(task));
        when(packets.findLatestByTaskId(task.taskId())).thenReturn(Optional.of(mock(BusinessDecisionPacket.class)));
        when(associations.findSkillIdByTaskId(task.taskId())).thenReturn(Optional.of(skillId));
        for (String prompt : new String[] { null, "", "  \n" }) {
            when(skills.findActiveById(skillId)).thenReturn(Optional.of(skill(skillId, prompt)));
            assertThat(service.find(task.taskId(), identity(userId)).available()).isFalse();
        }
        when(skills.findActiveById(skillId)).thenReturn(Optional.empty());
        assertThat(service.find(task.taskId(), identity(userId)).available()).isFalse();
        when(associations.findSkillIdByTaskId(task.taskId())).thenReturn(Optional.empty());
        assertThat(service.find(task.taskId(), identity(userId)).available()).isFalse();
    }

    @Test
    void hidesEntryWithoutPacket() {
        UUID userId = UUID.randomUUID();
        InvestigationTask task = task(userId);
        when(tasks.findById(task.taskId())).thenReturn(Optional.of(task));

        assertThat(service.find(task.taskId(), identity(userId)).available()).isFalse();
        verifyNoInteractions(associations, skills);
    }

    @Test
    void deniesOtherUserBeforeReadingScene() {
        InvestigationTask task = task(UUID.randomUUID());
        when(tasks.findById(task.taskId())).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> service.find(task.taskId(), identity(UUID.randomUUID())))
                .isInstanceOf(RepositoryEntityNotFoundException.class);
        verifyNoInteractions(packets, associations, skills);
    }

    private static CurrentIdentity identity(UUID userId) {
        return new CurrentIdentity(userId, "test", "测试用户", Set.of(RoleCode.OPERATOR));
    }

    private static InvestigationTask task(UUID userId) {
        Instant now = Instant.parse("2026-11-02T00:00:00Z");
        return new InvestigationTask(UUID.randomUUID(), "核对凭证", "业务说明", userId, TaskStatus.DRAFT,
                UUID.randomUUID(), null, null, null, now, now, 0, null, userId, userId);
    }

    private static BusinessSceneSkill skill(UUID skillId, String prompt) {
        UUID userId = UUID.randomUUID();
        Instant now = Instant.parse("2026-11-02T00:00:00Z");
        return new BusinessSceneSkill(skillId, "凭证核对", "调查", null, prompt, userId, now, userId, now);
    }
}
