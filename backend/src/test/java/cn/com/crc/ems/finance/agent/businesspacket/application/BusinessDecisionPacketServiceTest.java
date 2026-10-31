package cn.com.crc.ems.finance.agent.businesspacket.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
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
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.application.GuidanceDraftRepository;
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
        var guidanceDraftRepository = mock(GuidanceDraftRepository.class);
        UUID taskId = UUID.randomUUID();
        UUID skillId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(taskId)));
        when(evidenceRepository.findByTaskIdOrderByRound(taskId)).thenReturn(List.of(accum(taskId)));
        when(guidanceDraftRepository.findLatestDraftJsonByTaskId(taskId)).thenReturn(Optional.of(draftJson("逐轮修订结论")));
        when(taskSkillRepository.findSkillIdByTaskId(taskId)).thenReturn(Optional.of(skillId));
        when(sceneSkillRepository.findActiveById(skillId)).thenReturn(Optional.of(skill(skillId)));
        var suggestion = new BusinessPacketAnalysisSuggestion("离职人员 E001", "仍有 3 条生效权限", "回收生效权限",
                List.of("先冻结"), "已核对证据");
        when(modelClient.analyzeDecisionPacket(any())).thenReturn(new BusinessModelResult<>(suggestion,
                new BusinessModelCallMetadata("LIVE", "deepseek-chat", "a".repeat(64), "b".repeat(64), 10, 100)));
        var service = new BusinessDecisionPacketService(packetRepository, evidenceRepository, modelClient, taskRepository,
                taskSkillRepository, sceneSkillRepository, emsConfigService, guidanceDraftRepository,
                Clock.fixed(NOW, ZoneOffset.UTC));

        service.generate(taskId);

        ArgumentCaptor<BusinessPacketAnalysisContext> contextCaptor = ArgumentCaptor
                .forClass(BusinessPacketAnalysisContext.class);
        verify(modelClient).analyzeDecisionPacket(contextCaptor.capture());
        assertThat(contextCaptor.getValue().title()).isEqualTo("离职权限处置");
        assertThat(contextCaptor.getValue().evidence()).hasSize(1);
        assertThat(contextCaptor.getValue().decisionPacketPrompt()).isEqualTo("决策包提示词");
        assertThat(contextCaptor.getValue().guidanceDraft().conclusion()).isEqualTo("逐轮修订结论");
        assertThat(contextCaptor.getValue().guidanceDraft().evidence()).hasSize(1);
        ArgumentCaptor<BusinessDecisionPacket> packetCaptor = ArgumentCaptor.forClass(BusinessDecisionPacket.class);
        verify(packetRepository).insert(packetCaptor.capture());
        assertThat(packetCaptor.getValue().revision()).isEqualTo(1);
        assertThat(packetCaptor.getValue().canonicalContent()).contains("sceneSkillName");
        assertThat(packetCaptor.getValue().canonicalContent()).contains("guidanceDraft");
        assertThat(packetCaptor.getValue().canonicalContent()).contains("逐轮修订结论");
        var packetContent = CanonicalJsonCodec.parse(packetCaptor.getValue().canonicalContent());
        assertThat(packetContent.path("evidence").size()).isEqualTo(1);
        assertThat(packetContent.path("guidanceDraft").path("evidence").size()).isEqualTo(1);
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
        var guidanceDraftRepository = mock(GuidanceDraftRepository.class);
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(taskId)));
        when(evidenceRepository.findByTaskIdOrderByRound(taskId)).thenReturn(List.of());
        when(guidanceDraftRepository.findLatestDraftJsonByTaskId(taskId)).thenReturn(Optional.of(draftJson("结论")));
        var service = new BusinessDecisionPacketService(packetRepository, evidenceRepository, modelClient, taskRepository,
                taskSkillRepository, sceneSkillRepository, emsConfigService, guidanceDraftRepository,
                Clock.fixed(NOW, ZoneOffset.UTC));

        assertThatThrownBy(() -> service.generate(taskId)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void rejectsGenerationWithoutSavedGuidanceDraft() {
        var packetRepository = mock(BusinessDecisionPacketRepository.class);
        var evidenceRepository = mock(BusinessEvidenceAccumRepository.class);
        var modelClient = mock(BusinessModelClient.class);
        var taskRepository = mock(InvestigationTaskRepository.class);
        var taskSkillRepository = mock(TaskBusinessSceneSkillRepository.class);
        var sceneSkillRepository = mock(BusinessSceneSkillRepository.class);
        var emsConfigService = mock(EmsDatabaseConnectionConfigService.class);
        var guidanceDraftRepository = mock(GuidanceDraftRepository.class);
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(taskId)));
        when(evidenceRepository.findByTaskIdOrderByRound(taskId)).thenReturn(List.of(accum(taskId)));
        var service = new BusinessDecisionPacketService(packetRepository, evidenceRepository, modelClient, taskRepository,
                taskSkillRepository, sceneSkillRepository, emsConfigService, guidanceDraftRepository,
                Clock.fixed(NOW, ZoneOffset.UTC));

        assertThatThrownBy(() -> service.generate(taskId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("调查草稿");
        org.mockito.Mockito.verifyNoInteractions(modelClient);
    }

    @Test
    void changedGuidanceDraftCreatesNewRevisionWithoutChangingOldPacket() {
        var packetRepository = mock(BusinessDecisionPacketRepository.class);
        var evidenceRepository = mock(BusinessEvidenceAccumRepository.class);
        var modelClient = mock(BusinessModelClient.class);
        var taskRepository = mock(InvestigationTaskRepository.class);
        var guidanceDraftRepository = mock(GuidanceDraftRepository.class);
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(taskId)));
        when(evidenceRepository.findByTaskIdOrderByRound(taskId)).thenReturn(List.of(accum(taskId)));
        when(guidanceDraftRepository.findLatestDraftJsonByTaskId(taskId))
                .thenReturn(Optional.of(draftJson("初次结论")), Optional.of(draftJson("修订结论")));
        var suggestion = new BusinessPacketAnalysisSuggestion("离职人员 E001", "模型结论", "回收权限", List.of(), "理由");
        when(modelClient.analyzeDecisionPacket(any())).thenReturn(new BusinessModelResult<>(suggestion,
                new BusinessModelCallMetadata("LIVE", "deepseek-chat", "a".repeat(64), "b".repeat(64), 10, 100)));
        var service = new BusinessDecisionPacketService(packetRepository, evidenceRepository, modelClient, taskRepository,
                null, null, mock(EmsDatabaseConnectionConfigService.class), guidanceDraftRepository,
                Clock.fixed(NOW, ZoneOffset.UTC));

        BusinessDecisionPacket first = service.generate(taskId).packet();
        when(packetRepository.findLatestByTaskId(taskId)).thenReturn(Optional.of(first));
        BusinessDecisionPacket second = service.generate(taskId).packet();

        assertThat(first.revision()).isEqualTo(1);
        assertThat(second.revision()).isEqualTo(2);
        assertThat(first.canonicalContent()).contains("初次结论").doesNotContain("修订结论");
        assertThat(second.canonicalContent()).contains("修订结论");
        assertThat(second.contentDigest()).isNotEqualTo(first.contentDigest());
        ArgumentCaptor<BusinessPacketAnalysisContext> contexts = ArgumentCaptor.forClass(BusinessPacketAnalysisContext.class);
        verify(modelClient, times(2)).analyzeDecisionPacket(contexts.capture());
        assertThat(contexts.getAllValues().get(1).guidanceDraft().conclusion()).isEqualTo("修订结论");
    }

    @Test
    void rejectsDamagedGuidanceDraftBeforeCallingModel() {
        var packetRepository = mock(BusinessDecisionPacketRepository.class);
        var evidenceRepository = mock(BusinessEvidenceAccumRepository.class);
        var modelClient = mock(BusinessModelClient.class);
        var taskRepository = mock(InvestigationTaskRepository.class);
        var guidanceDraftRepository = mock(GuidanceDraftRepository.class);
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(taskId)));
        when(guidanceDraftRepository.findLatestDraftJsonByTaskId(taskId)).thenReturn(Optional.of("{}"));
        var service = new BusinessDecisionPacketService(packetRepository, evidenceRepository, modelClient, taskRepository,
                null, null, mock(EmsDatabaseConnectionConfigService.class), guidanceDraftRepository,
                Clock.fixed(NOW, ZoneOffset.UTC));

        assertThatThrownBy(() -> service.generate(taskId))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("调查草稿无法读取");
        org.mockito.Mockito.verifyNoInteractions(modelClient);
    }

    @Test
    void usesLatestSnapshotOnly() {
        var packetRepository = mock(BusinessDecisionPacketRepository.class);
        var evidenceRepository = mock(BusinessEvidenceAccumRepository.class);
        var modelClient = mock(BusinessModelClient.class);
        var taskRepository = mock(InvestigationTaskRepository.class);
        var taskSkillRepository = mock(TaskBusinessSceneSkillRepository.class);
        var sceneSkillRepository = mock(BusinessSceneSkillRepository.class);
        var emsConfigService = mock(EmsDatabaseConnectionConfigService.class);
        var guidanceDraftRepository = mock(GuidanceDraftRepository.class);
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task(taskId)));
        when(evidenceRepository.findByTaskIdOrderByRound(taskId)).thenReturn(List.of(
                new BusinessEvidenceAccum(UUID.randomUUID(), taskId, 1,
                        "[{\"schema\":\"basedata\",\"table\":\"old_table\",\"purpose\":\"旧证据\",\"columns\":[\"id\"],\"rows\":[[\"1\"]]}]",
                        false, NOW),
                new BusinessEvidenceAccum(UUID.randomUUID(), taskId, 2,
                        "[{\"schema\":\"basedata\",\"table\":\"latest_table\",\"purpose\":\"最新快照\",\"columns\":[\"id\"],\"rows\":[[\"1\"]]}]",
                        true, NOW)));
        when(guidanceDraftRepository.findLatestDraftJsonByTaskId(taskId)).thenReturn(Optional.of(draftJson("最新结论")));
        var suggestion = new BusinessPacketAnalysisSuggestion("对象", "结论", "目标", List.of(), "理由");
        when(modelClient.analyzeDecisionPacket(any())).thenReturn(new BusinessModelResult<>(suggestion,
                new BusinessModelCallMetadata("LIVE", "deepseek-chat", "a".repeat(64), "b".repeat(64), 10, 100)));
        var service = new BusinessDecisionPacketService(packetRepository, evidenceRepository, modelClient, taskRepository,
                taskSkillRepository, sceneSkillRepository, emsConfigService, guidanceDraftRepository,
                Clock.fixed(NOW, ZoneOffset.UTC));

        service.generate(taskId);

        ArgumentCaptor<BusinessPacketAnalysisContext> contextCaptor = ArgumentCaptor
                .forClass(BusinessPacketAnalysisContext.class);
        verify(modelClient).analyzeDecisionPacket(contextCaptor.capture());
        assertThat(contextCaptor.getValue().evidence()).extracting(item -> item.table())
                .containsExactly("latest_table");
    }

    private static String draftJson(String conclusion) {
        return "{\"subjectKey\":\"离职人员 E001\",\"objectKey\":\"审批权限\","
                + "\"recommendedGoal\":\"回收权限\",\"alternativeGoals\":[],\"reason\":\"调查证据\","
                + "\"conclusion\":\"" + conclusion + "\",\"evidence\":[{\"schema\":\"basedata\","
                + "\"table\":\"account\",\"purpose\":\"核对生效权限\",\"columns\":[\"id\"],\"rows\":[[\"1\"]]}]}";
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
