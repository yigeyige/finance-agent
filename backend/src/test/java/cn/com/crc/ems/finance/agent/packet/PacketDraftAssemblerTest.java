package cn.com.crc.ems.finance.agent.packet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import cn.com.crc.ems.finance.agent.governance.application.RuntimeBundleRepository;
import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundle;
import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundleStatus;
import cn.com.crc.ems.finance.agent.packet.application.PacketAssemblyCommand;
import cn.com.crc.ems.finance.agent.packet.application.PacketDocumentValidator;
import cn.com.crc.ems.finance.agent.packet.application.PacketDraftAssembler;
import cn.com.crc.ems.finance.agent.packet.application.PacketDraftRepository;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.ScopeRevisionRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskStateTransitionCommand;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.ScopeRevision;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskAuditEventAppender;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskStateTransitionService;

/**
 *
 * @Description: 验证决策包草稿组装完成后进入待复核状态，避免发布前重查被草稿状态错误阻断。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-30 23:20:00
 */
class PacketDraftAssemblerTest {

    private static final Instant NOW = Instant.parse("2026-09-30T15:20:00Z");
    private static final UUID TASK_ID = UUID.randomUUID();
    private static final UUID SCOPE_ID = UUID.randomUUID();
    private static final UUID BUNDLE_ID = UUID.randomUUID();
    private static final UUID ACTOR_ID = UUID.randomUUID();
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    /**
     *
     * @description: 组装当前草稿后持久化草稿并按任务状态机推进为待复核。
     *
     * @author: yigeyige99
     */
    @Test
    void assemblesDraftBeforeTransitioningTaskToWaitingReview() throws Exception {
        PacketDraftRepository drafts = mock(PacketDraftRepository.class);
        InvestigationTaskRepository tasks = mock(InvestigationTaskRepository.class);
        ScopeRevisionRepository scopes = mock(ScopeRevisionRepository.class);
        RuntimeBundleRepository bundles = mock(RuntimeBundleRepository.class);
        TaskAuditEventAppender audit = mock(TaskAuditEventAppender.class);
        TaskStateTransitionService transitions = mock(TaskStateTransitionService.class);
        InvestigationTask task = task();
        when(tasks.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(scopes.findById(SCOPE_ID)).thenReturn(Optional.of(scope()));
        when(bundles.findPublishedById(BUNDLE_ID)).thenReturn(Optional.of(bundle()));
        when(drafts.findCurrentByTaskId(TASK_ID)).thenReturn(Optional.empty());
        when(drafts.findLatestByTaskId(TASK_ID)).thenReturn(Optional.empty());
        PacketDraftAssembler assembler = new PacketDraftAssembler(drafts, tasks, scopes, bundles, audit, transitions,
                new PacketDocumentValidator(), Clock.fixed(NOW, ZoneOffset.UTC));

        assembler.assemble(command());

        ArgumentCaptor<TaskStateTransitionCommand> transition = ArgumentCaptor.forClass(TaskStateTransitionCommand.class);
        verify(transitions).transition(transition.capture());
        assertThat(transition.getValue().taskId()).isEqualTo(TASK_ID);
        assertThat(transition.getValue().targetStatus()).isEqualTo(TaskStatus.WAITING_REVIEW);
        assertThat(transition.getValue().actorUserId()).isEqualTo(ACTOR_ID);
        assertThat(transition.getValue().occurredAt()).isEqualTo(NOW);
        var order = inOrder(drafts, transitions);
        order.verify(drafts).insert(any());
        order.verify(transitions).transition(any());
    }

    private static PacketAssemblyCommand command() throws Exception {
        JsonNode document = JSON_MAPPER.readTree("""
                {
                  "premises": {}, "scope": {}, "sources": [],
                  "facts": {"current": [], "future": [], "undetermined": []},
                  "computations": {}, "professionalJudgments": [], "recommendations": [],
                  "businessConfirmations": {"impactConfirmed": true, "pendingItems": []},
                  "validationPreparation": {}, "handoff": {}
                }
                """);
        return new PacketAssemblyCommand(TASK_ID, SCOPE_ID, BUNDLE_ID, "检查配置漂移", document.path("premises"),
                document.path("scope"), document.path("sources"), document.path("facts"),
                document.path("computations"), document.path("professionalJudgments"), document.path("recommendations"),
                document.path("businessConfirmations"), document.path("validationPreparation"), document.path("handoff"),
                ACTOR_ID);
    }

    private static InvestigationTask task() {
        return new InvestigationTask(TASK_ID, "合成验证", "检查配置漂移", ACTOR_ID, TaskStatus.DRAFT_PACKET, BUNDLE_ID,
                null, SCOPE_ID, null, NOW, NOW, 5, null, ACTOR_ID, ACTOR_ID);
    }

    private static ScopeRevision scope() {
        VersionedJson document = new VersionedJson("scope-v1", "{}");
        return new ScopeRevision(SCOPE_ID, TASK_ID, 1, "synthetic", document, document, document, document, document,
                document, document, ACTOR_ID, NOW, "a".repeat(64), "scope-v1", ACTOR_ID, NOW, ACTOR_ID, NOW);
    }

    private static RuntimeBundle bundle() {
        return new RuntimeBundle(BUNDLE_ID, "0.1.0", "2.0.3", "model-v1", "prompt-v1", "rule-v1", "tool-v1",
                "source-v1", null, "b".repeat(64), RuntimeBundleStatus.PUBLISHED, NOW, ACTOR_ID, NOW, ACTOR_ID);
    }
}
