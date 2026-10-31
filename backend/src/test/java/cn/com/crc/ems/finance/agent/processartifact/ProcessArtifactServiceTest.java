package cn.com.crc.ems.finance.agent.processartifact;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.businesspacket.application.BusinessDecisionPacketRepository;
import cn.com.crc.ems.finance.agent.businesspacket.domain.BusinessDecisionPacket;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelCallMetadata;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.processartifact.application.ProcessArtifactBatchRepository;
import cn.com.crc.ems.finance.agent.processartifact.application.ProcessArtifactService;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactPlan;
import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.scenario.domain.BusinessSceneSkill;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;

/** 验证文件生成只读取决策包快照，并在前置条件失败时保留原批次。 */
class ProcessArtifactServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-31T03:00:00Z");
    private static final UUID SKILL_ID = UUID.fromString("a6a050ae-b699-4ca0-af72-4381e990eda0");

    @Test
    void rejectsOldPacketWithoutProcessSnapshotBeforeModelCall() {
        UUID taskId = UUID.randomUUID();
        var packets = mock(BusinessDecisionPacketRepository.class);
        var model = mock(BusinessModelClient.class);
        var batches = mock(ProcessArtifactBatchRepository.class);
        var skills = mock(BusinessSceneSkillRepository.class);
        var packet = packet(taskId, "{\"guidanceDraft\":{},\"recommendedGoal\":\"方案\"}");
        when(packets.findLatestByTaskId(taskId)).thenReturn(Optional.of(packet));
        var service = new ProcessArtifactService(packets, skills, model, batches,
                Clock.fixed(NOW, ZoneOffset.UTC));

        assertThatThrownBy(() -> service.generate(taskId, packet.packetId())).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("重新调查");
        verifyNoInteractions(model, batches);
    }

    @Test
    void generatesFromFixedPacketAndStoresModelFile() {
        UUID taskId = UUID.randomUUID();
        var packets = mock(BusinessDecisionPacketRepository.class);
        var model = mock(BusinessModelClient.class);
        var batches = mock(ProcessArtifactBatchRepository.class);
        var skills = mock(BusinessSceneSkillRepository.class);
        String planJson = """
                {"name":"测试流程","nodes":[{"id":"draft","kind":"draft","name":"起草","assignee":""}],
                 "flows":[{"source":"start","target":"draft","condition":null},
                          {"source":"draft","target":"end","condition":null}],
                 "nodeParameters":[],"departmentApprovals":[],"approvalRules":[],"missingItems":[],"unsupported":[]}
                """;
        String packetJson = "{\"recommendedGoal\":\"按结论生成\",\"sceneSkillId\":\"" + SKILL_ID
                + "\",\"guidanceDraft\":{\"processPlan\":" + planJson + "},\"evidence\":[]}";
        var packet = packet(taskId, packetJson);
        when(packets.findLatestByTaskId(taskId)).thenReturn(Optional.of(packet));
        when(skills.findActiveById(SKILL_ID)).thenReturn(Optional.of(BusinessSceneSkill.create(SKILL_ID,
                "审批流程生成-隆地", "调查", "决策", "生成提示词", UUID.randomUUID(), NOW)));
        var plan = ProcessArtifactPlan.fromJson(CanonicalJsonCodec.parse(planJson));
        when(model.generateProcessArtifacts(any())).thenReturn(new BusinessModelResult<>(plan,
                new BusinessModelCallMetadata("STUB", "本地模拟模型", "a".repeat(64), "b".repeat(64), 0, 1)));
        when(batches.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var service = new ProcessArtifactService(packets, skills, model, batches,
                Clock.fixed(NOW, ZoneOffset.UTC));

        var result = service.generate(taskId, packet.packetId());

        assertThat(result.packetRevision()).isEqualTo(1);
        assertThat(result.files()).extracting("kind").containsExactly("MODEL");
        assertThat(result.files().get(0).fileName()).isEqualTo("测试流程BPMN.xml");
    }

    private static BusinessDecisionPacket packet(UUID taskId, String content) {
        return new BusinessDecisionPacket(UUID.randomUUID(), taskId, 1,
                CanonicalJsonCodec.canonicalize(CanonicalJsonCodec.parse(content)), "a".repeat(64), NOW);
    }
}
