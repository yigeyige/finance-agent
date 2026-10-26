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

import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillService;
import cn.com.crc.ems.finance.agent.scenario.domain.BusinessSceneSkill;

/** 验证业务场景 Skill 的创建、编辑和软删除边界。 */
class BusinessSceneSkillServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-08T15:00:00Z");
    private static final UUID OPERATOR = UUID.randomUUID();

    @Test
    void createsValidatedSkill() {
        BusinessSceneSkillRepository repository = mock(BusinessSceneSkillRepository.class);
        BusinessSceneSkillService service = new BusinessSceneSkillService(repository, clock());

        BusinessSceneSkill skill = service.create("人员变更处理", "核查人员变更后的权限", "INVESTIGATE", "人员变更", null,
                OPERATOR);

        assertThat(skill.name()).isEqualTo("人员变更处理");
        assertThat(skill.createdBy()).isEqualTo(OPERATOR);
        assertThat(skill.createdAt()).isEqualTo(NOW);
        verify(repository).insert(skill);
    }

    @Test
    void revisesExistingActiveSkill() {
        BusinessSceneSkillRepository repository = mock(BusinessSceneSkillRepository.class);
        UUID skillId = UUID.randomUUID();
        BusinessSceneSkill existing = BusinessSceneSkill.create(skillId, "人员变更处理", "旧说明", "INVESTIGATE", "人员变更",
                null, OPERATOR, NOW.minusSeconds(60));
        when(repository.findActiveById(skillId)).thenReturn(Optional.of(existing));
        when(repository.update(any())).thenReturn(true);
        BusinessSceneSkillService service = new BusinessSceneSkillService(repository, clock());

        BusinessSceneSkill revised = service.update(skillId, "人员调整处理", "新说明", "ADJUST", "人员调整", null, OPERATOR);

        assertThat(revised.name()).isEqualTo("人员调整处理");
        assertThat(revised.handlingType()).isEqualTo("ADJUST");
        assertThat(revised.updatedAt()).isEqualTo(NOW);
        verify(repository).update(revised);
    }

    @Test
    void rejectsDeletingUnavailableSkill() {
        BusinessSceneSkillRepository repository = mock(BusinessSceneSkillRepository.class);
        UUID skillId = UUID.randomUUID();
        when(repository.softDelete(skillId, OPERATOR, NOW)).thenReturn(false);
        BusinessSceneSkillService service = new BusinessSceneSkillService(repository, clock());

        assertThatThrownBy(() -> service.delete(skillId, OPERATOR)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("business scene skill is unavailable");
    }

    private static Clock clock() {
        return Clock.fixed(NOW, ZoneOffset.UTC);
    }
}
