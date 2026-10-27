package cn.com.crc.ems.finance.agent.businesspacket.application;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.com.crc.ems.finance.agent.businesspacket.domain.BusinessDecisionPacket;
import cn.com.crc.ems.finance.agent.businesspacket.domain.BusinessEvidenceAccum;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.DecisionPacketSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseEvidence;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJson;
import cn.com.crc.ems.finance.agent.shared.domain.JsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;
import cn.com.crc.ems.finance.agent.task.application.GuidanceDraftRepository;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;

/**
 *
 * @Description: 组装业务决策包，读取最新草稿与累积证据并按 schema、table、purpose 去重合并。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-24 10:00:00
 */
@Service
public class BusinessDecisionPacketService {

    private static final int MAX_EVIDENCE = 50;
    private static final String EVIDENCE_FIELD = "evidence";

    private final BusinessDecisionPacketRepository packetRepository;
    private final BusinessEvidenceAccumRepository evidenceRepository;
    private final GuidanceDraftRepository guidanceDraftRepository;
    private final InvestigationTaskRepository taskRepository;
    private final BusinessModelClient modelClient;
    private final Clock clock;

    public BusinessDecisionPacketService(BusinessDecisionPacketRepository packetRepository,
            BusinessEvidenceAccumRepository evidenceRepository, GuidanceDraftRepository guidanceDraftRepository,
            InvestigationTaskRepository taskRepository, BusinessModelClient modelClient, Clock clock) {
        this.packetRepository = packetRepository;
        this.evidenceRepository = evidenceRepository;
        this.guidanceDraftRepository = guidanceDraftRepository;
        this.taskRepository = taskRepository;
        this.modelClient = modelClient;
        this.clock = clock;
    }

    /**
     *
     * @description: 生成业务决策包；无草稿时拒绝，内容与上一修订一致时跳过不新增修订。
     *
     * @param: taskId 当前任务标识
     * @return: 生成结果，含新增或跳过的修订与提示文案
     * @throws: IllegalStateException 任务尚无业务处理草稿时抛出
     * @author: yigeyige99
     */
    @Transactional
    public BusinessDecisionPacketResult generate(UUID taskId) {
        String draftJson = guidanceDraftRepository.findLatestDraftJsonByTaskId(taskId)
                .orElseThrow(() -> new IllegalStateException("尚未生成业务处理草稿，无法生成业务决策包"));
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("业务处理任务不存在"));
        List<EmsDatabaseEvidence> deduplicated = deduplicate(evidenceRepository.findByTaskIdOrderByRound(taskId));
        DecisionPacketSuggestion suggestion = modelClient.generateDecisionPacket(
                new BusinessGuidanceContext(taskId, task.title(), task.originalQuestion(), null, null, null,
                        List.of(), List.of()),
                deduplicated).value();
        ObjectNode businessContent = buildBusinessContent(suggestion, deduplicated);
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

    private ObjectNode buildBusinessContent(DecisionPacketSuggestion suggestion, List<EmsDatabaseEvidence> evidence) {
        ObjectNode content = JsonCodec.mapper().createObjectNode();
        content.put("action", suggestion.action());
        content.putPOJO("targets", suggestion.targets());
        content.set(EVIDENCE_FIELD, JsonCodec.mapper().valueToTree(evidence));
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
}
