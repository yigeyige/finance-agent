package cn.com.crc.ems.finance.agent.businesspacket.application;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.com.crc.ems.finance.agent.businesspacket.domain.BusinessDecisionPacket;
import cn.com.crc.ems.finance.agent.businesspacket.domain.BusinessEvidenceAccum;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseConnectionConfigService;
import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.BusinessHandlingDraft;
import cn.com.crc.ems.finance.agent.harness.application.BusinessPacketAnalysisContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessPacketAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseEvidence;
import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.scenario.domain.BusinessSceneSkill;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJson;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.JsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.GuidanceDraftRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskBusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;

/**
 *
 * @Description: 组装业务决策包：固定最新调查草稿、任务信息与去重证据，调用模型分析后固化修订；模型调用在保存事务外。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-28 12:00:00
 */
@Service
public class BusinessDecisionPacketService {

    private static final int MAX_EVIDENCE = 50;

    private final BusinessDecisionPacketRepository packetRepository;
    private final BusinessEvidenceAccumRepository evidenceRepository;
    private final BusinessModelClient modelClient;
    private final InvestigationTaskRepository taskRepository;
    private final TaskBusinessSceneSkillRepository taskBusinessSceneSkillRepository;
    private final BusinessSceneSkillRepository businessSceneSkillRepository;
    private final EmsDatabaseConnectionConfigService emsDatabaseConfigService;
    private final GuidanceDraftRepository guidanceDraftRepository;
    private final Clock clock;

    public BusinessDecisionPacketService(BusinessDecisionPacketRepository packetRepository,
            BusinessEvidenceAccumRepository evidenceRepository, BusinessModelClient modelClient,
            InvestigationTaskRepository taskRepository, TaskBusinessSceneSkillRepository taskBusinessSceneSkillRepository,
            BusinessSceneSkillRepository businessSceneSkillRepository,
            EmsDatabaseConnectionConfigService emsDatabaseConfigService,
            GuidanceDraftRepository guidanceDraftRepository, Clock clock) {
        this.packetRepository = packetRepository;
        this.evidenceRepository = evidenceRepository;
        this.modelClient = modelClient;
        this.taskRepository = taskRepository;
        this.taskBusinessSceneSkillRepository = taskBusinessSceneSkillRepository;
        this.businessSceneSkillRepository = businessSceneSkillRepository;
        this.emsDatabaseConfigService = emsDatabaseConfigService;
        this.guidanceDraftRepository = guidanceDraftRepository;
        this.clock = clock;
    }

    /**
     *
     * @description: 固定最新调查草稿、任务信息与已有证据，调用模型分析后生成业务决策包；内容与上一修订一致时跳过不新增修订。
     *
     * @param: taskId 当前任务标识
     * @return: 生成结果，含新增或跳过的修订与提示文案
     * @throws: IllegalStateException 任务不存在、无调查草稿、无证据或模型调用失败时抛出
     * @author: yigeyige99
     */
    public BusinessDecisionPacketResult generate(UUID taskId) {
        InvestigationTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("业务处理任务不存在或已失效"));
        String draftJson = guidanceDraftRepository.findLatestDraftJsonByTaskId(taskId)
                .orElseThrow(() -> new IllegalStateException("尚无调查草稿，请先完成调查"));
        BusinessHandlingDraft guidanceDraft;
        try {
            guidanceDraft = BusinessHandlingDraft.fromJson(CanonicalJsonCodec.parse(draftJson));
        } catch (RuntimeException exception) {
            throw new IllegalStateException("调查草稿无法读取，请重新完成调查", exception);
        }
        List<EmsDatabaseEvidence> evidence = deduplicate(evidenceRepository.findByTaskIdOrderByRound(taskId));
        BusinessSceneSkill sceneSkill = findSceneSkill(taskId);
        String decisionPacketPrompt = sceneSkill == null ? null : sceneSkill.decisionPacketPrompt();
        BusinessPacketAnalysisSuggestion analysis = modelClient
                .analyzeDecisionPacket(new BusinessPacketAnalysisContext(taskId, task.title(), task.originalQuestion(),
                        evidence, decisionPacketPrompt, guidanceDraft))
                .value();
        ObjectNode businessContent = buildBusinessContent(task, sceneSkill, analysis, evidence, guidanceDraft);
        String contentDigest = CanonicalJson.of(businessContent.toString()).sha256();
        var latest = packetRepository.findLatestByTaskId(taskId);
        if (latest.isPresent() && latest.get().contentDigest().equals(contentDigest)) {
            return new BusinessDecisionPacketResult(latest.get(), false, "内容未变化，未生成新修订");
        }
        int revision = latest.map(packet -> packet.revision() + 1).orElse(1);
        Instant now = Instant.now(clock);
        UUID packetId = UuidGenerator.newUuid();
        ObjectNode fullContent = buildFullContent(packetId, taskId, revision, now, businessContent);
        BusinessDecisionPacket packet = new BusinessDecisionPacket(packetId, taskId, revision,
                CanonicalJson.of(fullContent.toString()).value(), contentDigest, now);
        try {
            packetRepository.insert(packet);
        } catch (DuplicateKeyException exception) {
            throw new IllegalStateException("业务决策包已被并发生成，请刷新后重试", exception);
        }
        return new BusinessDecisionPacketResult(packet, true, "业务决策包已生成");
    }

    /** 读取任务全部累积证据，供前端证据面板展示跨轮过程。 */
    public List<BusinessEvidenceAccum> listAccumulatedEvidence(UUID taskId) {
        return evidenceRepository.findByTaskIdOrderByRound(taskId);
    }

    /** 读取最新业务决策包，不存在时返回空。 */
    public java.util.Optional<BusinessDecisionPacket> latest(UUID taskId) {
        return packetRepository.findLatestByTaskId(taskId);
    }

    /** 读取指定修订的业务决策包，不存在时返回空。 */
    public java.util.Optional<BusinessDecisionPacket> revision(UUID taskId, int revision) {
        return packetRepository.findByTaskIdAndRevision(taskId, revision);
    }

    private ObjectNode buildBusinessContent(InvestigationTask task, BusinessSceneSkill sceneSkill,
            BusinessPacketAnalysisSuggestion analysis, List<EmsDatabaseEvidence> evidence,
            BusinessHandlingDraft guidanceDraft) {
        ObjectNode content = JsonCodec.mapper().createObjectNode();
        content.put("subjectKey", analysis.subjectKey());
        content.put("conclusion", analysis.conclusion());
        content.put("recommendedGoal", analysis.recommendedGoal());
        content.putPOJO("alternativeGoals", analysis.alternativeGoals());
        content.put("reason", analysis.reason());
        if (sceneSkill != null) {
            content.put("sceneSkillId", sceneSkill.skillId().toString());
            content.put("sceneSkillName", sceneSkill.name());
        }
        if (task.databaseConfigId() != null) {
            EmsDatabaseConnectionConfig instance = emsDatabaseConfigService.resolveEnabled(task.databaseConfigId());
            content.put("databaseConfigId", instance.configId().toString());
            content.put("databaseInstanceName", instance.name());
        }
        content.set("evidence", JsonCodec.mapper().valueToTree(evidence));
        content.set("guidanceDraft", guidanceDraft.toJson());
        return content;
    }

    private ObjectNode buildFullContent(UUID packetId, UUID taskId, int revision, Instant createdAt,
            ObjectNode businessContent) {
        ObjectNode full = JsonCodec.mapper().createObjectNode();
        full.put("packetId", packetId.toString());
        full.put("taskId", taskId.toString());
        full.put("revision", revision);
        full.put("createdAt", createdAt.toString());
        businessContent.fieldNames().forEachRemaining(name -> full.set(name, businessContent.get(name)));
        return full;
    }

    private List<EmsDatabaseEvidence> deduplicate(List<BusinessEvidenceAccum> accums) {
        LinkedHashMap<String, EmsDatabaseEvidence> merged = new LinkedHashMap<>();
        for (BusinessEvidenceAccum accum : accums) {
            for (EmsDatabaseEvidence evidence : parseEvidence(accum.evidenceJson())) {
                merged.put(evidence.schema() + "\u0000" + evidence.table() + "\u0000" + evidence.purpose(),
                        evidence);
            }
        }
        List<EmsDatabaseEvidence> result = new ArrayList<>(merged.values());
        if (result.size() > MAX_EVIDENCE) {
            return List.copyOf(result.subList(result.size() - MAX_EVIDENCE, result.size()));
        }
        return List.copyOf(result);
    }

    private List<EmsDatabaseEvidence> parseEvidence(String evidenceJson) {
        try {
            return JsonCodec.mapper().readValue(evidenceJson, new TypeReference<List<EmsDatabaseEvidence>>() { });
        } catch (Exception exception) {
            throw new IllegalStateException("证据工作区数据不可解析", exception);
        }
    }

    private BusinessSceneSkill findSceneSkill(UUID taskId) {
        if (taskBusinessSceneSkillRepository == null || businessSceneSkillRepository == null) {
            return null;
        }
        return taskBusinessSceneSkillRepository.findSkillIdByTaskId(taskId)
                .map(skillId -> businessSceneSkillRepository.findActiveById(skillId)
                        .orElseThrow(() -> new IllegalStateException("任务关联的业务场景 Skill 已失效，请重新创建任务")))
                .orElse(null);
    }
}
