package cn.com.crc.ems.finance.agent.businessintake;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.businessintake.application.BusinessIntakeDraftService;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseConnectionConfigService;
import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;
import cn.com.crc.ems.finance.agent.harness.application.BusinessIntakeContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessIntakeDraft;
import cn.com.crc.ems.finance.agent.harness.application.BusinessIntakeSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelCallMetadata;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillService;
import cn.com.crc.ems.finance.agent.scenario.domain.BusinessSceneSkill;

/**
 * @Description: 验证 AI 预填的名称匹配、重名/未命中留空、长度校验与截断。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 16:00:00
 */
class BusinessIntakeDraftServiceTest {

    private static final String DIGEST = "0".repeat(64);
    private static final UUID USER = UUID.randomUUID();

    @Test
    void matchesUniqueNamesAndTruncatesLongText() {
        BusinessSceneSkill scene = scene(UUID.randomUUID(), "费用报销核查");
        EmsDatabaseConnectionConfig database = database(UUID.randomUUID(), "财务主库");
        BusinessModelClient modelClient = modelClient(new BusinessIntakeSuggestion(
                "标".repeat(400), "说".repeat(9000), " 费用报销核查 ", "财务主库"));
        BusinessIntakeDraftService service = service(modelClient, List.of(scene), List.of(database));

        BusinessIntakeDraft draft = service.draft("请核查九月费用报销中的重复审批单据");

        assertThat(draft.sceneSkillId()).isEqualTo(scene.skillId());
        assertThat(draft.sceneSkillName()).isEqualTo("费用报销核查");
        assertThat(draft.databaseConfigId()).isEqualTo(database.configId());
        assertThat(draft.title()).hasSize(300).endsWith("…");
        assertThat(draft.description()).hasSize(8000).endsWith("…");
    }

    @Test
    void leavesDuplicateAndUnknownNamesEmpty() {
        BusinessSceneSkill first = scene(UUID.randomUUID(), "重复场景");
        BusinessSceneSkill second = scene(UUID.randomUUID(), "重复场景");
        BusinessModelClient modelClient = modelClient(new BusinessIntakeSuggestion(
                "标题", "说明", "重复场景", "不存在的库"));
        BusinessIntakeDraftService service = service(modelClient, List.of(first, second), List.of());

        BusinessIntakeDraft draft = service.draft("请核查重复场景下的审批配置问题");

        assertThat(draft.sceneSkillId()).isNull();
        assertThat(draft.sceneSkillName()).isNull();
        assertThat(draft.databaseConfigId()).isNull();
        assertThat(draft.databaseName()).isNull();
    }

    @Test
    void keepsParsedFieldsWhenModelReturnsPartialResult() {
        BusinessModelClient modelClient = modelClient(new BusinessIntakeSuggestion("仅标题", "", "", ""));
        BusinessIntakeDraftService service = service(modelClient, List.of(), List.of());

        BusinessIntakeDraft draft = service.draft("请核查费用报销中的重复审批单据");

        assertThat(draft.title()).isEqualTo("仅标题");
        assertThat(draft.description()).isEmpty();
    }

    @Test
    void rejectsTextOutsideLengthWindow() {
        BusinessIntakeDraftService service = service(modelClient(new BusinessIntakeSuggestion("", "", "", "")),
                List.of(), List.of());

        assertThatThrownBy(() -> service.draft("太短"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("10–2000");
        assertThatThrownBy(() -> service.draft("长".repeat(2001)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("10–2000");
    }

    private static BusinessIntakeDraftService service(BusinessModelClient modelClient,
            List<BusinessSceneSkill> scenes, List<EmsDatabaseConnectionConfig> databases) {
        BusinessSceneSkillService sceneService = mock(BusinessSceneSkillService.class);
        when(sceneService.list()).thenReturn(scenes);
        EmsDatabaseConnectionConfigService databaseService = mock(EmsDatabaseConnectionConfigService.class);
        when(databaseService.listEnabled()).thenReturn(databases);
        return new BusinessIntakeDraftService(modelClient, sceneService, databaseService);
    }

    private static BusinessModelClient modelClient(BusinessIntakeSuggestion suggestion) {
        BusinessModelClient modelClient = mock(BusinessModelClient.class);
        when(modelClient.extractBusinessIntake(any(BusinessIntakeContext.class)))
                .thenReturn(new BusinessModelResult<>(suggestion,
                        new BusinessModelCallMetadata("LIVE", "deepseek-chat", DIGEST, DIGEST, 120, 8)));
        return modelClient;
    }

    private static BusinessSceneSkill scene(UUID skillId, String name) {
        Instant now = Instant.parse("2026-11-06T00:00:00Z");
        return new BusinessSceneSkill(skillId, name, "调查提示词", null, USER, now, USER, now);
    }

    private static EmsDatabaseConnectionConfig database(UUID configId, String name) {
        Instant now = Instant.parse("2026-11-06T00:00:00Z");
        return new EmsDatabaseConnectionConfig(configId, name, "jdbc:mysql://127.0.0.1:3306/ems", "reader",
                "cipher", 5, 10, 100, true, USER, now, USER, now);
    }
}
