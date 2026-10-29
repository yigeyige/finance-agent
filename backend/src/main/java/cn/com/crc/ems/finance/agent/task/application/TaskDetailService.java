package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import cn.com.crc.ems.finance.agent.businesspacket.application.BusinessDecisionPacketRepository;
import cn.com.crc.ems.finance.agent.businesspacket.domain.BusinessDecisionPacket;
import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationStep;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.ScopeRevision;
import cn.com.crc.ems.finance.agent.task.domain.TaskPremise;

/**
 *
 * @Description: 组装任务列表双击后展示的只读业务处理详情，不返回执行密钥或模型原文。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-09 20:45:00
 */
@Service
public class TaskDetailService {
    private final InvestigationTaskRepository taskRepository;
    private final TaskPremiseRepository premiseRepository;
    private final ScopeRevisionRepository scopeRevisionRepository;
    private final InvestigationStepRepository stepRepository;
    private final GuidanceDraftRepository guidanceDraftRepository;
    private final BusinessDecisionPacketRepository decisionPacketRepository;

    public TaskDetailService(InvestigationTaskRepository taskRepository, TaskPremiseRepository premiseRepository,
            ScopeRevisionRepository scopeRevisionRepository, InvestigationStepRepository stepRepository,
            GuidanceDraftRepository guidanceDraftRepository,
            BusinessDecisionPacketRepository decisionPacketRepository) {
        this.taskRepository = taskRepository;
        this.premiseRepository = premiseRepository;
        this.scopeRevisionRepository = scopeRevisionRepository;
        this.stepRepository = stepRepository;
        this.guidanceDraftRepository = guidanceDraftRepository;
        this.decisionPacketRepository = decisionPacketRepository;
    }

    /**
     *
     * @description: 查询当前身份有权查看的任务业务、范围与方案只读详情。
     *
     * @param: taskId 任务标识
     * @param: identity 当前可信身份
     * @return: 任务只读详情
     * @throws: RepositoryEntityNotFoundException 任务不存在或当前身份无权访问时抛出
     * @author: yigeyige99
     */
    @Transactional(readOnly = true)
    public Detail find(UUID taskId, CurrentIdentity identity) {
        InvestigationTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RepositoryEntityNotFoundException("业务处理任务不存在或无权查看"));
        if (!identity.hasRole(RoleCode.PLATFORM_ADMIN) && !task.initiatorUserId().equals(identity.userId())) {
            throw new RepositoryEntityNotFoundException("业务处理任务不存在或无权查看");
        }
        ScopeRevision scope = task.currentScopeRevisionId() == null ? null
                : scopeRevisionRepository.findById(task.currentScopeRevisionId()).orElse(null);
        GuidanceDraftProjection guidance = guidanceDraftRepository.findLatestDraftJsonByTaskId(task.taskId())
                .map(TaskDetailService::projectGuidance).orElse(GuidanceDraftProjection.EMPTY);
        DecisionPacket decisionPacket = decisionPacketRepository.findLatestByTaskId(task.taskId())
                .map(TaskDetailService::projectDecisionPacket).orElse(null);
        return new Detail(task.taskId(), task.title(), task.originalQuestion(), task.status().name(), task.initiatorUserId(), task.createdAt(),
                premiseRepository.findByTaskId(task.taskId()).stream().map(Premise::from).toList(), Scope.from(scope),
                stepRepository.findByTaskId(task.taskId()).stream().map(PlanStep::from).toList(),
                guidance.conclusion(), guidance.evidence(), decisionPacket);
    }

    private static GuidanceDraftProjection projectGuidance(String draftJson) {
        try {
            JsonNode draft = CanonicalJsonCodec.parse(draftJson);
            JsonNode evidence = draft.path("evidence");
            return new GuidanceDraftProjection(draft.path("conclusion").asText(""),
                    evidence.isArray() ? evidence : JsonNodeFactory.instance.arrayNode());
        } catch (RuntimeException ignored) {
            return GuidanceDraftProjection.EMPTY;
        }
    }

    public record GuidanceDraftProjection(String conclusion, JsonNode evidence) {
        static final GuidanceDraftProjection EMPTY = new GuidanceDraftProjection("",
                JsonNodeFactory.instance.arrayNode());
    }

    /** 投影最新业务决策包的修订号、处理对象、结论、推荐方案与去重后证据，不存在时返回空。 */
    private static DecisionPacket projectDecisionPacket(BusinessDecisionPacket packet) {
        try {
            JsonNode content = CanonicalJsonCodec.parse(packet.canonicalContent());
            JsonNode evidence = content.path("evidence");
            return new DecisionPacket(packet.revision(), content.path("subjectKey").asText(""),
                    content.path("conclusion").asText(""), content.path("recommendedGoal").asText(""),
                    textArray(content.path("alternativeGoals")), content.path("reason").asText(""),
                    content.path("sceneSkillName").asText(""), content.path("databaseInstanceName").asText(""),
                    evidence.isArray() ? evidence : JsonNodeFactory.instance.arrayNode());
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    /** 读取文本数组字段，缺失或非数组时返回空列表，兼容历史包。 */
    private static List<String> textArray(JsonNode value) {
        if (value == null || !value.isArray()) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        value.forEach(item -> {
            if (item.isTextual()) {
                result.add(item.textValue());
            }
        });
        return List.copyOf(result);
    }

    public record Detail(UUID taskId, String title, String description, String status, UUID initiatorUserId, Instant createdAt,
            List<Premise> premises, Scope scope, List<PlanStep> plans, String conclusion, @JsonRawValue JsonNode evidence,
            DecisionPacket decisionPacket) { }
    public record DecisionPacket(int revision, String subjectKey, String conclusion, String recommendedGoal,
            List<String> alternativeGoals, String reason, String sceneSkillName, String databaseInstanceName,
            @JsonRawValue JsonNode evidence) { }
    public record Premise(String subjectKey) {
        static Premise from(TaskPremise value) { return new Premise(value.subjectKey()); }
    }
    public record Scope(String objectSelector, int revisionNo) {
        static Scope from(ScopeRevision value) { return value == null ? null : new Scope(value.objectSelector().canonical().value(), value.revisionNo()); }
    }
    public record PlanStep(int sequenceNo, String goal, boolean recommended, String status) {
        static PlanStep from(InvestigationStep value) { return new PlanStep(value.sequenceNo(), value.goal(), value.recommended(), value.status().name()); }
    }
}
