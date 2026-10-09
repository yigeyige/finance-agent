package cn.com.crc.ems.finance.agent.task.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/** 验证任务列表按可信身份选择受限或全量分页查询。 */
class TaskListServiceTest {

    @Test
    void platformAdministratorQueriesAllTasksByPage() {
        InvestigationTaskRepository repository = mock(InvestigationTaskRepository.class);
        TaskPage expected = new TaskPage(List.of(), 0, 2, 20);
        when(repository.findPage(2, 20, TaskStatus.READY, "权限")).thenReturn(expected);
        TaskListService service = new TaskListService(repository);
        CurrentIdentity identity = identity(RoleCode.PLATFORM_ADMIN);

        TaskPage result = service.list(identity, 2, 20, TaskStatus.READY, " 权限 ");

        assertThat(result).isSameAs(expected);
        verify(repository).findPage(2, 20, TaskStatus.READY, "权限");
    }

    @Test
    void operatorQueriesOnlyOwnTasksByPage() {
        InvestigationTaskRepository repository = mock(InvestigationTaskRepository.class);
        CurrentIdentity identity = identity(RoleCode.OPERATOR);
        TaskPage expected = new TaskPage(List.of(), 0, 1, 20);
        when(repository.findPageByInitiator(identity.userId(), 1, 20, null, null)).thenReturn(expected);
        TaskListService service = new TaskListService(repository);

        TaskPage result = service.list(identity, 1, 20, null, "  ");

        assertThat(result).isSameAs(expected);
        verify(repository).findPageByInitiator(identity.userId(), 1, 20, null, null);
    }

    private static CurrentIdentity identity(RoleCode role) {
        return new CurrentIdentity(UUID.randomUUID(), "test-subject", "测试用户", Set.of(role));
    }
}
