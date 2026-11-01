package cn.com.crc.ems.finance.agent.task.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.businesspacket.application.BusinessDecisionPacketRepository;
import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/**
 * @Description: 验证任务详情名称投影及权限检查先于展示名称查询。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-01 00:00:00
 */
class TaskDetailDisplayServiceTest {
    @Test
    void returnsDisplayNamesAfterAuthorization() {
        Fixture fixture = new Fixture();
        UUID userId = UUID.randomUUID();
        InvestigationTask task = task(userId);
        fixture.stubTask(task);
        when(fixture.names.displayNames(task)).thenReturn(new TaskListRowService.DisplayNames("张三", "凭证核对", "共享库"));

        TaskDetailService.Detail detail = fixture.service.find(task.taskId(), identity(userId));

        assertThat(detail.initiatorName()).isEqualTo("张三");
        assertThat(detail.businessSceneName()).isEqualTo("凭证核对");
        assertThat(detail.databaseInstanceName()).isEqualTo("共享库");
    }

    @Test
    void deniesOtherUserBeforeNameLookup() {
        Fixture fixture = new Fixture();
        InvestigationTask task = task(UUID.randomUUID());
        when(fixture.tasks.findById(task.taskId())).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> fixture.service.find(task.taskId(), identity(UUID.randomUUID())))
                .isInstanceOf(RepositoryEntityNotFoundException.class);
        verifyNoInteractions(fixture.names);
    }

    private static CurrentIdentity identity(UUID userId) {
        return new CurrentIdentity(userId, "test", "测试用户", Set.of(RoleCode.OPERATOR));
    }

    private static InvestigationTask task(UUID userId) {
        Instant now = Instant.parse("2026-11-01T00:00:00Z");
        return new InvestigationTask(UUID.randomUUID(), "核对凭证", "业务说明", userId, TaskStatus.DRAFT,
                UUID.randomUUID(), null, null, null, now, now, 0, null, userId, userId);
    }

    private static class Fixture {
        final InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        final TaskPremiseRepository premises = mock(TaskPremiseRepository.class);
        final ScopeRevisionRepository scopes = mock(ScopeRevisionRepository.class);
        final InvestigationStepRepository steps = mock(InvestigationStepRepository.class);
        final GuidanceDraftRepository guidance = mock(GuidanceDraftRepository.class);
        final BusinessDecisionPacketRepository packets = mock(BusinessDecisionPacketRepository.class);
        final TaskListRowService names = mock(TaskListRowService.class);
        final TaskDetailService service = new TaskDetailService(tasks, premises, scopes, steps, guidance, packets, names);

        void stubTask(InvestigationTask task) {
            when(tasks.findById(task.taskId())).thenReturn(Optional.of(task));
            when(premises.findByTaskId(task.taskId())).thenReturn(List.of());
            when(steps.findByTaskId(task.taskId())).thenReturn(List.of());
            when(guidance.findLatestDraftJsonByTaskId(task.taskId())).thenReturn(Optional.empty());
            when(packets.findLatestByTaskId(task.taskId())).thenReturn(Optional.empty());
        }
    }
}
