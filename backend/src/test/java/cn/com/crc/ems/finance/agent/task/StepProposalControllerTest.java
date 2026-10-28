package cn.com.crc.ems.finance.agent.task;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cn.com.crc.ems.finance.agent.task.application.ApprovalDecision;
import cn.com.crc.ems.finance.agent.task.application.StepApprovalApplicationService;
import cn.com.crc.ems.finance.agent.task.application.StepApprovalResult;
import cn.com.crc.ems.finance.agent.task.application.StepProposalApplicationService;
import cn.com.crc.ems.finance.agent.task.application.StepProposalResult;
import cn.com.crc.ems.finance.agent.task.domain.ApprovalStatus;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.StepApproval;
import cn.com.crc.ems.finance.agent.task.domain.StepStatus;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.web.StepProposalController;
import cn.com.crc.ems.finance.agent.task.web.StepProposalExceptionHandler;

/** 验证步骤提议和审批 REST 的头校验、未知字段拒绝及 permit 不回显。 */
class StepProposalControllerTest {

    private static final UUID ACTOR = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final Instant NOW = Instant.parse("2026-09-27T03:00:00Z");

    @Test
    void createsBoundedProposalWithRequiredHeaders() throws Exception {
        StepProposalApplicationService proposals = mock(StepProposalApplicationService.class);
        StepApprovalApplicationService approvals = mock(StepApprovalApplicationService.class);
        UUID taskId = UUID.randomUUID();
        UUID scopeId = UUID.randomUUID();
        UUID stepId = UUID.randomUUID();
        InvestigationTask task = task(taskId, TaskStatus.WAITING_STEP_APPROVAL, scopeId, stepId);
        InvestigationStep step = new InvestigationStep(stepId, taskId, 1, "读取配置", true,
                StepStatus.PROPOSED, NOW, null, ACTOR, NOW, ACTOR);
        when(proposals.propose(any())).thenReturn(new StepProposalResult(task, List.of(step)));

        mvc(proposals, approvals).perform(post("/api/v1/tasks/{taskId}/step-proposals", taskId)
                .contentType(MediaType.APPLICATION_JSON).header("X-Actor-User-Id", ACTOR)
                .header("Idempotency-Key", "step-proposal-0001")
                .content("{\"steps\":[{\"goal\":\"读取配置\",\"recommended\":true}]}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.steps[0].stepId").value(stepId.toString()));
    }

    @Test
    void rejectsUnknownProposalField() throws Exception {
        StepProposalApplicationService proposals = mock(StepProposalApplicationService.class);
        StepApprovalApplicationService approvals = mock(StepApprovalApplicationService.class);

        mvc(proposals, approvals).perform(post("/api/v1/tasks/{taskId}/step-proposals", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON).header("X-Actor-User-Id", ACTOR)
                .header("Idempotency-Key", "step-proposal-0002")
                .content("{\"steps\":[{\"goal\":\"读取配置\",\"recommended\":true}],\"reasoning\":\"no\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(proposals);
    }

    @Test
    void approvalResponseDoesNotContainPermit() throws Exception {
        StepProposalApplicationService proposals = mock(StepProposalApplicationService.class);
        StepApprovalApplicationService approvals = mock(StepApprovalApplicationService.class);
        UUID taskId = UUID.randomUUID();
        UUID stepId = UUID.randomUUID();
        UUID scopeId = UUID.randomUUID();
        InvestigationTask task = task(taskId, TaskStatus.WAITING_STEP_APPROVAL, scopeId, stepId);
        StepApproval approval = new StepApproval(UUID.randomUUID(), stepId, 1, ACTOR, NOW,
                NOW.plusSeconds(600), "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                UUID.randomUUID(), scopeId, "field-v1", "query-v1", 2, 0,
                "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb", ApprovalStatus.ACTIVE,
                ACTOR, NOW, ACTOR, NOW);
        when(approvals.approve(any())).thenReturn(new StepApprovalResult(task, approval, ApprovalDecision.APPROVED));

        mvc(proposals, approvals).perform(post("/api/v1/tasks/{taskId}/step-approvals", taskId)
                .contentType(MediaType.APPLICATION_JSON).header("X-Actor-User-Id", ACTOR)
                .header("Idempotency-Key", "step-approval-0001")
                .content("{\"stepId\":\"" + stepId + "\",\"goalHash\":\""
                        + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                        + "\",\"sourceBindingId\":\"" + approval.sourceBindingId()
                        + "\",\"scopeRevisionId\":\"" + scopeId
                        + "\",\"fieldPolicyVersion\":\"field-v1\",\"queryTemplateVersion\":\"query-v1\","
                        + "\"maxCalls\":2,\"expectedRowVersion\":2,\"decision\":\"APPROVED\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.permit").doesNotExist());
    }

    private static MockMvc mvc(StepProposalApplicationService proposals, StepApprovalApplicationService approvals) {
        return MockMvcBuilders.standaloneSetup(new StepProposalController(proposals, approvals,
                Clock.fixed(NOW, ZoneOffset.UTC))).setControllerAdvice(new StepProposalExceptionHandler()).build();
    }

    private static InvestigationTask task(UUID taskId, TaskStatus status, UUID scopeId, UUID stepId) {
        return new InvestigationTask(taskId, "title", "question", ACTOR, status, UUID.randomUUID(), null, scopeId, stepId,
                NOW, NOW, 2, null, ACTOR, ACTOR);
    }
}
