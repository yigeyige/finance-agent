package cn.com.crc.ems.finance.agent.packet.application;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.com.crc.ems.finance.agent.governance.application.RuntimeBundleRepository;
import cn.com.crc.ems.finance.agent.packet.domain.PacketDraft;
import cn.com.crc.ems.finance.agent.packet.domain.PacketDraftStatus;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJson;
import cn.com.crc.ems.finance.agent.shared.domain.JsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.ScopeRevisionRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskStateTransitionCommand;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskAuditEventAppender;
import cn.com.crc.ems.finance.agent.task.infrastructure.TaskStateTransitionService;

/**
 *
 * @Description: 组装规范决策包草稿、生成摘要绑定的 HTML 快照，并将任务推进到待复核状态。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-30 23:20:00
 */
@Service
public class PacketDraftAssembler {

    private final PacketDraftRepository draftRepository;
    private final InvestigationTaskRepository taskRepository;
    private final ScopeRevisionRepository scopeRevisionRepository;
    private final RuntimeBundleRepository runtimeBundleRepository;
    private final TaskAuditEventAppender auditEventAppender;
    private final TaskStateTransitionService taskStateTransitionService;
    private final PacketDocumentValidator validator;
    private final Clock applicationClock;

    public PacketDraftAssembler(PacketDraftRepository draftRepository, InvestigationTaskRepository taskRepository,
            ScopeRevisionRepository scopeRevisionRepository, RuntimeBundleRepository runtimeBundleRepository,
            TaskAuditEventAppender auditEventAppender, TaskStateTransitionService taskStateTransitionService,
            PacketDocumentValidator validator, Clock applicationClock) {
        this.draftRepository = draftRepository;
        this.taskRepository = taskRepository;
        this.scopeRevisionRepository = scopeRevisionRepository;
        this.runtimeBundleRepository = runtimeBundleRepository;
        this.auditEventAppender = auditEventAppender;
        this.taskStateTransitionService = taskStateTransitionService;
        this.validator = validator;
        this.applicationClock = applicationClock;
    }

    /**
     *
     * @description: 组装当前范围下的单个草稿，提交后将任务从草稿状态推进到待复核；已有当前草稿时拒绝覆盖。
     *
     * @param: command 草稿组装命令
     * @return: 已保存草稿及其 HTML 快照
     * @throws: IllegalStateException 当前范围、运行版本或草稿状态不满足组装条件
     * @author: yigeyige99
     */
    @Transactional
    public PacketDraftAssemblyResult assemble(PacketAssemblyCommand command) {
        requireCommand(command);
        var task = taskRepository.findById(command.taskId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("investigation task not found"));
        var scope = scopeRevisionRepository.findById(command.scopeRevisionId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("scope revision not found"));
        if (!task.taskId().equals(scope.taskId()) || !command.scopeRevisionId().equals(task.currentScopeRevisionId())) {
            throw new IllegalStateException("scope revision is not current for task");
        }
        runtimeBundleRepository.findPublishedById(command.runtimeBundleId())
                .orElseThrow(() -> new RepositoryEntityNotFoundException("published runtime bundle not found"));
        if (!task.taskId().equals(command.taskId())) {
            throw new IllegalArgumentException("task identity mismatch");
        }
        if (draftRepository.findCurrentByTaskId(command.taskId()).isPresent()) {
            throw new IllegalStateException("current packet draft already exists");
        }
        ObjectNode document = JsonCodec.mapper().createObjectNode();
        document.put("problem", command.problem().trim());
        document.set("premises", command.premises());
        document.set("scope", command.scope());
        document.set("sources", command.sources());
        document.set("facts", command.facts());
        document.set("computations", command.computations());
        document.set("professionalJudgments", command.professionalJudgments());
        document.set("recommendations", command.recommendations());
        document.set("businessConfirmations", command.businessConfirmations());
        document.set("validationPreparation", command.validationPreparation());
        document.set("handoff", command.handoff());
        validator.validate(document);
        CanonicalJson canonical = CanonicalJson.of(document.toString());
        Instant now = Instant.now(applicationClock);
        int nextDraftNo = draftRepository.findLatestByTaskId(command.taskId()).map(draft -> draft.draftNo() + 1).orElse(1);
        PacketDraft draft = new PacketDraft(UuidGenerator.newUuid(), command.taskId(), nextDraftNo,
                command.scopeRevisionId(), command.runtimeBundleId(), new VersionedJson("packet-v1", canonical.value()),
                canonical.sha256(), PacketDraftStatus.CURRENT, command.actorUserId(), now, command.actorUserId(), now);
        draftRepository.insert(draft);
        taskStateTransitionService.transition(new TaskStateTransitionCommand(command.taskId(), TaskStatus.WAITING_REVIEW,
                "packet draft assembled", command.actorUserId(), now, true));
        String html = htmlSnapshot(canonical.value(), canonical.sha256());
        auditEventAppender.append(UuidGenerator.newUuid(), command.taskId(), command.actorUserId(),
                "PACKET_DRAFT_ASSEMBLED", "packet_draft", draft.draftId().toString(), now, payload -> {
                    payload.put("draftNo", draft.draftNo());
                    payload.put("scopeRevisionId", command.scopeRevisionId().toString());
                    payload.put("contentDigest", draft.contentDigest());
                });
        return new PacketDraftAssemblyResult(draft, html);
    }

    private static String htmlSnapshot(String canonicalJson, String digest) {
        return "<!doctype html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\"><title>决策包草稿</title></head>"
                + "<body><main><h1>决策包草稿</h1><p>内容摘要：<code>" + HtmlUtils.htmlEscape(digest)
                + "</code></p><pre>" + HtmlUtils.htmlEscape(canonicalJson) + "</pre></main></body></html>";
    }

    private static void requireCommand(PacketAssemblyCommand command) {
        if (command == null || command.taskId() == null || command.scopeRevisionId() == null
                || command.runtimeBundleId() == null || command.actorUserId() == null || command.problem() == null
                || command.problem().isBlank()) {
            throw new IllegalArgumentException("packet assembly command is incomplete");
        }
    }
}
