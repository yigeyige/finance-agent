package cn.com.crc.ems.finance.agent.businesspacket.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import cn.com.crc.ems.finance.agent.businesspacket.domain.BusinessDecisionPacket;
import cn.com.crc.ems.finance.agent.businesspacket.domain.BusinessEvidenceAccum;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseConnectionConfigService;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelCallMetadata;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.harness.application.BusinessPacketAnalysisContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessPacketAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.scenario.domain.BusinessSceneSkill;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.TaskBusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/** 验证业务决策包生成组装固定输入、调用模型并固化修订。 */
class BusinessDecisionPacketServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-28T02:00:00Z");
    private static final UUID ACTOR = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void generatesPacketFromModelAnalysis() {
        var packetRepository = mock(BusinessDecisionPacketRepository.class);
        var evidenceRepository = mock(BusinessEvidenceAccumRepository.class);
        var modelClient = mock(BusinessModelClient.class);
        var taskRepository = mock(InvestigationTaskRepository.class);
        var taskSkillRepository = mock(TaskBusinessSceneSkillRepository.class);
        var sceneSkillRepository = mock(BusinessSceneSkillRepository.class);
        var emsConfigService = mock(EmsDatabaseConnectionConfigService.class);
        UUID taskId = UUID.randomUUID();
        UUID skillId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(taskId)));
        when(evidenceRepository.findByTaskIdOrderByRound(taskId)).thenReturn(List.of(accum(taskId)));
        when(taskSkillRepository.findSkillIdByTaskId(taskId)).thenReturn(Optional.of(skillId));
        when(sceneSkillRepository.findActiveById(skillId)).thenReturn(Optional.of(skill(skillId)));
        var suggestion = new BusinessPacketAnalysisSuggestion("离职人员 E001", "仍有 3 条生效权限", "回收生效权限",
                List.of("先冻结"), "已核对证据");
        when(modelClient.analyzeDecisionPacket(any())).thenReturn(new BusinessModelResult<>(suggestion,
                new BusinessModelCallMetadata("LIVE", "deepseek-chat", "a".repeat(64), "b".repeat(64), 10, 100)));
        var service = new BusinessDecisionPacketService(packetRepository, evidenceRepository, modelClient, taskRepository,
                taskSkillRepository, sceneSkillRepository, emsConfigService, Clock.fixed(NOW, ZoneOffset.UTC));

        service.generate(taskId);

        ArgumentCaptor<BusinessPacketAnalysisContext> contextCaptor = ArgumentCaptor
                .forClass(BusinessPacketAnalysisContext.class);
        verify(modelClient).analyzeDecisionPacket(contextCaptor.capture());
        assertThat(contextCaptor.getValue().title()).isEqualTo("离职权限处置");
        assertThat(contextCaptor.getValue().evidence()).hasSize(1);
        assertThat(contextCaptor.getValue().decisionPacketPrompt()).isEqualTo("决策包提示词");
        ArgumentCaptor<BusinessDecisionPacket> packetCaptor = ArgumentCaptor.forClass(BusinessDecisionPacket.class);
        verify(packetRepository).insert(packetCaptor.capture());
        assertThat(packetCaptor.getValue().revision()).isEqualTo(1);
        assertThat(packetCaptor.getValue().canonicalContent()).contains("sceneSkillName");
    }

    @Test
    void rejectsGenerationWithoutEvidence() {
        var packetRepository = mock(BusinessDecisionPacketRepository.class);
        var evidenceRepository = mock(BusinessEvidenceAccumRepository.class);
        var modelClient = mock(BusinessModelClient.class);
        var taskRepository = mock(InvestigationTaskRepository.class);
        var taskSkillRepository = mock(TaskBusinessSceneSkillRepository.class);
        var sceneSkillRepository = mock(BusinessSceneSkillRepository.class);
        var emsConfigService = mock(EmsDatabaseConnectionConfigService.class);
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(taskId)));
        when(evidenceRepository.findByTaskIdOrderByRound(taskId)).thenReturn(List.of());
        var service = new BusinessDecisionPacketService(packetRepository, evidenceRepository, modelClient, taskRepository,
                taskSkillRepository, sceneSkillRepository, emsConfigService, Clock.fixed(NOW, ZoneOffset.UTC));

        assertThatThrownBy(() -> service.generate(taskId)).isInstanceOf(IllegalStateException.class);
    }

    private static InvestigationTask task(UUID taskId) {
        return new InvestigationTask(taskId, "离职权限处置", "核查离职人员生效权限", ACTOR, TaskStatus.DRAFT,
                UUID.randomUUID(), null, null, null, NOW, NOW, 0, null, ACTOR, ACTOR);
    }

    private static BusinessEvidenceAccum accum(UUID taskId) {
        return new BusinessEvidenceAccum(UUID.randomUUID(), taskId, 1,
                "[{\"schema\":\"basedata\",\"table\":\"account\",\"purpose\":\"核对生效权限\",\"columns\":[\"id\"],\"rows\":[[\"1\"]]}]",
                NOW);
    }

    private static BusinessSceneSkill skill(UUID skillId) {
        return BusinessSceneSkill.create(skillId, "场景名称", "调查提示词", "决策包提示词", ACTOR, NOW);
    }
}
