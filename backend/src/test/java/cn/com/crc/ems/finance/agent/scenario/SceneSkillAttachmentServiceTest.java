package cn.com.crc.ems.finance.agent.scenario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.scenario.application.SceneSkillAttachmentRepository;
import cn.com.crc.ems.finance.agent.scenario.application.SceneSkillAttachmentService;
import cn.com.crc.ems.finance.agent.scenario.domain.BusinessSceneSkill;

/** 验证场景附件上传的数量、摘要与格式校验。 */
class SceneSkillAttachmentServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-30T02:00:00Z");
    private static final UUID OPERATOR = UUID.fromString("00000000-0000-4000-8000-000000000001");

    @Test
    void rejectsExceedingMaxAttachments() {
        var attachmentRepository = mock(SceneSkillAttachmentRepository.class);
        var skillRepository = mock(BusinessSceneSkillRepository.class);
        UUID skillId = UUID.randomUUID();
        when(skillRepository.findActiveById(skillId)).thenReturn(Optional.of(skill(skillId)));
        when(attachmentRepository.countBySkillId(skillId)).thenReturn(5);
        var service = new SceneSkillAttachmentService(attachmentRepository, skillRepository,
                Clock.fixed(NOW, ZoneOffset.UTC));

        assertThatThrownBy(() -> service.upload(skillId, file("a.json", "{\"k\":1}"), "摘要", OPERATOR))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("最多");
    }

    @Test
    void rejectsBlankSummary() {
        var attachmentRepository = mock(SceneSkillAttachmentRepository.class);
        var skillRepository = mock(BusinessSceneSkillRepository.class);
        UUID skillId = UUID.randomUUID();
        when(skillRepository.findActiveById(skillId)).thenReturn(Optional.of(skill(skillId)));
        when(attachmentRepository.countBySkillId(skillId)).thenReturn(0);
        var service = new SceneSkillAttachmentService(attachmentRepository, skillRepository,
                Clock.fixed(NOW, ZoneOffset.UTC));

        assertThatThrownBy(() -> service.upload(skillId, file("a.json", "{\"k\":1}"), "  ", OPERATOR))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("摘要");
    }

    @Test
    void uploadsAttachmentWithTextProjection() {
        var attachmentRepository = mock(SceneSkillAttachmentRepository.class);
        var skillRepository = mock(BusinessSceneSkillRepository.class);
        UUID skillId = UUID.randomUUID();
        when(skillRepository.findActiveById(skillId)).thenReturn(Optional.of(skill(skillId)));
        when(attachmentRepository.countBySkillId(skillId)).thenReturn(0);
        var service = new SceneSkillAttachmentService(attachmentRepository, skillRepository,
                Clock.fixed(NOW, ZoneOffset.UTC));

        var result = service.upload(skillId, file("a.json", "{\"key\":\"value\"}"), "审批配置模板", OPERATOR);

        assertThat(result.summary()).isEqualTo("审批配置模板");
        assertThat(result.contentText()).isEqualTo("{\"key\":\"value\"}");
        assertThat(result.contentType()).isEqualTo("application/json");
        verify(attachmentRepository).insert(any());
    }

    private static MockMultipartFile file(String name, String content) {
        return new MockMultipartFile("file", name, "application/octet-stream", content.getBytes());
    }

    private static BusinessSceneSkill skill(UUID skillId) {
        return BusinessSceneSkill.create(skillId, "人员变更处理", "调查提示词", "决策包提示词", OPERATOR, NOW);
    }
}
