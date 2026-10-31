package cn.com.crc.ems.finance.agent.processartifact.application;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import cn.com.crc.ems.finance.agent.businesspacket.application.BusinessDecisionPacketRepository;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.processartifact.domain.EmsProcessArtifactConverter;
import cn.com.crc.ems.finance.agent.processartifact.domain.EmsProcessArtifactFiles;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactBatch;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactFile;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactPlan;
import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.scenario.domain.BusinessSceneSkill;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;

/** 从指定决策包快照生成 EMS 文件批次，不读取可变任务草稿。 */
@Service
public final class ProcessArtifactService {

    private static final UUID PROCESS_SKILL_ID = UUID.fromString("a6a050ae-b699-4ca0-af72-4381e990eda0");

    private final BusinessDecisionPacketRepository packets;
    private final BusinessSceneSkillRepository skills;
    private final BusinessModelClient model;
    private final ProcessArtifactBatchRepository batches;
    private final Clock clock;

    public ProcessArtifactService(BusinessDecisionPacketRepository packets, BusinessSceneSkillRepository skills,
            BusinessModelClient model, ProcessArtifactBatchRepository batches, Clock clock) {
        this.packets = packets;
        this.skills = skills;
        this.model = model;
        this.batches = batches;
        this.clock = clock;
    }

    /** 用户点击后才生成；模型与转换先完成，持久化失败时原批次仍在。 */
    public ProcessArtifactBatch generate(UUID taskId, UUID expectedPacketId) {
        var packet = packets.findLatestByTaskId(taskId)
                .orElseThrow(() -> new IllegalStateException("尚无决策包，请先生成决策包"));
        if (!packet.packetId().equals(expectedPacketId)) {
            throw new IllegalStateException("决策包已更新，请刷新后再生成文件");
        }
        JsonNode content = CanonicalJsonCodec.parse(packet.canonicalContent());
        JsonNode snapshot = content.path("guidanceDraft").path("processPlan");
        if (snapshot.isMissingNode() || snapshot.isNull()) {
            throw new IllegalStateException("决策包没有固定流程调查结论，请重新调查并生成决策包");
        }
        if (!PROCESS_SKILL_ID.toString().equals(content.path("sceneSkillId").asText())) {
            throw new IllegalStateException("当前决策包不属于审批流程生成场景");
        }
        ProcessArtifactPlan.fromJson(snapshot);
        BusinessSceneSkill skill = skills.findActiveById(PROCESS_SKILL_ID)
                .orElseThrow(() -> new IllegalStateException("审批流程生成场景已失效"));
        if (skill.processArtifactPrompt() == null || skill.processArtifactPrompt().isBlank()) {
            throw new IllegalStateException("审批流程文件生成提示词尚未配置");
        }
        ProcessArtifactPlan plan = model.generateProcessArtifacts(new ProcessArtifactModelContext(taskId,
                packet.packetId(), packet.revision(), content, skill.processArtifactPrompt())).value();
        if (packets.findLatestByTaskId(taskId).filter(latest -> latest.packetId().equals(packet.packetId()))
                .isEmpty()) throw new IllegalStateException("决策包已更新，请刷新后再生成文件");
        EmsProcessArtifactFiles converted = new EmsProcessArtifactConverter().convert(plan);
        UUID batchId = UUID.randomUUID();
        List<ProcessArtifactFile> files = new ArrayList<>();
        add(files, batchId, "MODEL", converted.fileName(), converted.modelJson());
        add(files, batchId, "NODE_PARAMETERS", "节点参数配置.json", converted.nodeParametersJson());
        add(files, batchId, "DEPARTMENT_APPROVALS", "部门审批流.json", converted.departmentApprovalsJson());
        add(files, batchId, "APPROVAL_RULES", "审批规则.json", converted.approvalRulesJson());
        Instant now = Instant.now(clock);
        return batches.save(new ProcessArtifactBatch(batchId, taskId, packet.packetId(), packet.revision(),
                CanonicalJsonCodec.canonicalize(plan.toJson()), converted.missingItems(), converted.importStatus(),
                now, List.copyOf(files)));
    }

    /** 展示最近两次成功生成的批次。 */
    public List<ProcessArtifactBatch> recent(UUID taskId) {
        return batches.findRecentByTaskId(taskId);
    }

    private static void add(List<ProcessArtifactFile> files, UUID batchId, String kind,
            String name, String content) {
        if (content != null) files.add(new ProcessArtifactFile(UUID.randomUUID(), batchId, kind, name, content));
    }
}
