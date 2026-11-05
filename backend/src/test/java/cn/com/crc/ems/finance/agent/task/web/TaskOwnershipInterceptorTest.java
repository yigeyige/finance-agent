package cn.com.crc.ems.finance.agent.task.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/**
 * @Description: 验证所有任务子路径按发起人隔离，管理员可操作全部。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-05 00:00:00
 */
class TaskOwnershipInterceptorTest {
    @AfterEach void clear() { IdentityContext.clear(); }

    @Test
    void rejectsOtherUsersTaskWriteButAllowsAdministrator() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        when(tasks.findById(taskId)).thenReturn(Optional.of(new InvestigationTask(taskId, "任务", "说明", owner,
                TaskStatus.DRAFT, UUID.randomUUID(), null, null, null, Instant.now(), Instant.now(), 0,
                null, owner, owner)));
        var guard = new TaskOwnershipInterceptor(tasks);
        var request = new MockHttpServletRequest("POST", "/api/v1/tasks/" + taskId + "/model-interactions/evidence-analysis");
        IdentityContext.set(new CurrentIdentity(UUID.randomUUID(), "other", "他人", Set.of(RoleCode.PLATFORM_USER)));
        var denied = new MockHttpServletResponse();
        assertThat(guard.preHandle(request, denied, null)).isFalse();
        assertThat(denied.getStatus()).isEqualTo(404);

        IdentityContext.set(new CurrentIdentity(UUID.randomUUID(), "admin2", "管理员", Set.of(RoleCode.PLATFORM_ADMIN)));
        assertThat(guard.preHandle(request, new MockHttpServletResponse(), null)).isTrue();
    }
}
