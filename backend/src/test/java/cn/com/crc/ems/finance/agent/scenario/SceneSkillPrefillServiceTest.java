package cn.com.crc.ems.finance.agent.scenario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseConnectionConfigService;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseQueryExecutor;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseTableMetadata;
import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelCallMetadata;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.harness.application.SceneSkillPrefillContext;
import cn.com.crc.ems.finance.agent.harness.application.SceneSkillPrefillSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.SceneSkillPrefillTarget;
import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillService;
import cn.com.crc.ems.finance.agent.scenario.application.SceneSkillPrefillCommand;
import cn.com.crc.ems.finance.agent.scenario.application.SceneSkillPrefillResult;
import cn.com.crc.ems.finance.agent.scenario.application.SceneSkillPrefillService;
import cn.com.crc.ems.finance.agent.scenario.application.SceneSkillTableSelector;

/**
 * @Description: 验证场景预填的前置依赖、权限、表名筛选、上下文字段与截断。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 18:00:00
 */
class SceneSkillPrefillServiceTest {

    private static final String DIGEST = "0".repeat(64);
    private static final CurrentIdentity IDENTITY = new CurrentIdentity(UUID.randomUUID(), "subject", "测试用户",
            Set.of(RoleCode.PLATFORM_USER));

    @Test
    void investigationUsesFilteredTablesAndReturnsName() {
        BusinessModelClient modelClient = modelClient(new SceneSkillPrefillSuggestion("费用报销核查", "调查提示词"));
        SceneSkillPrefillService service = service(modelClient, tables());
        UUID configId = UUID.randomUUID();

        SceneSkillPrefillResult result = service.prefill(new SceneSkillPrefillCommand(
                SceneSkillPrefillTarget.INVESTIGATION, "核查 basedata 中的费用报销数据", null, configId, "", "", ""), IDENTITY);

        assertThat(result.name()).isEqualTo("费用报销核查");
        assertThat(result.content()).isEqualTo("调查提示词");
        assertThat(result.tableCount()).isEqualTo(1);
        assertThat(result.warning()).isNull();
        ArgumentCaptor<SceneSkillPrefillContext> captor = ArgumentCaptor.forClass(SceneSkillPrefillContext.class);
        verify(modelClient).extractSceneSkillPrefill(captor.capture());
        assertThat(captor.getValue().tableNames()).containsExactly("basedata.employee_info");
    }

    @Test
    void decisionAndProcessDoNotReceiveTablesAndRequirePrerequisite() {
        BusinessModelClient modelClient = modelClient(new SceneSkillPrefillSuggestion("", "决策包提示词"));
        SceneSkillPrefillService service = service(modelClient, tables());
        UUID configId = UUID.randomUUID();

        SceneSkillPrefillResult decision = service.prefill(new SceneSkillPrefillCommand(
                SceneSkillPrefillTarget.DECISION_PACKET, "核查费用报销中的重复审批单据", null, configId, "场景",
                "已有调查提示词", ""), IDENTITY);
        assertThat(decision.name()).isEmpty();
        ArgumentCaptor<SceneSkillPrefillContext> captor = ArgumentCaptor.forClass(SceneSkillPrefillContext.class);
        verify(modelClient).extractSceneSkillPrefill(captor.capture());
        assertThat(captor.getValue().tableNames()).isEmpty();
        assertThat(captor.getValue().prerequisitePrompt()).isEqualTo("已有调查提示词");

        assertThatThrownBy(() -> service.prefill(new SceneSkillPrefillCommand(
                SceneSkillPrefillTarget.PROCESS_ARTIFACT, "核查费用报销中的重复审批单据", null, configId, "场景", "调查", ""), IDENTITY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("决策包提示词");
        verify(modelClient, org.mockito.Mockito.times(1)).extractSceneSkillPrefill(any(SceneSkillPrefillContext.class));
    }

    @Test
    void missingSchemaTablesProduceWarning() {
        BusinessModelClient modelClient = modelClient(new SceneSkillPrefillSuggestion("名称", "内容"));
        SceneSkillPrefillService service = service(modelClient, List.of(
                new EmsDatabaseTableMetadata("basedata", "oper_log", "")));
        UUID configId = UUID.randomUUID();

        SceneSkillPrefillResult result = service.prefill(new SceneSkillPrefillCommand(
                SceneSkillPrefillTarget.INVESTIGATION, "核查日志数据中的异常记录", null, configId, "", "", ""), IDENTITY);

        assertThat(result.tableCount()).isZero();
        assertThat(result.warning()).contains("未使用表结构");
    }

    @Test
    void editRequiresMaintainerAndTruncatesLongContent() {
        BusinessSceneSkillService sceneService = mock(BusinessSceneSkillService.class);
        BusinessModelClient modelClient = modelClient(new SceneSkillPrefillSuggestion("名".repeat(200),
                "文".repeat(9000)));
        SceneSkillPrefillService service = new SceneSkillPrefillService(modelClient, sceneService,
                mock(EmsDatabaseConnectionConfigService.class), mock(EmsDatabaseQueryExecutor.class),
                new SceneSkillTableSelector());
        UUID skillId = UUID.randomUUID();

        SceneSkillPrefillResult result = service.prefill(new SceneSkillPrefillCommand(
                SceneSkillPrefillTarget.INVESTIGATION, "核查费用报销中的重复审批单据", skillId, null, "", "", ""), IDENTITY);

        verify(sceneService).requireMaintainer(eq(skillId), eq(IDENTITY));
        assertThat(result.name()).hasSize(100).endsWith("…");
        assertThat(result.content()).hasSize(8000).endsWith("…");
    }

    private static SceneSkillPrefillService service(BusinessModelClient modelClient,
            List<EmsDatabaseTableMetadata> tables) {
        EmsDatabaseConnectionConfigService configService = mock(EmsDatabaseConnectionConfigService.class);
        when(configService.resolveEnabled(any())).thenReturn(mock(EmsDatabaseConnectionConfig.class));
        EmsDatabaseQueryExecutor executor = mock(EmsDatabaseQueryExecutor.class);
        when(executor.listAllowedTables(any())).thenReturn(tables);
        return new SceneSkillPrefillService(modelClient, mock(BusinessSceneSkillService.class), configService,
                executor, new SceneSkillTableSelector());
    }

    private static BusinessModelClient modelClient(SceneSkillPrefillSuggestion suggestion) {
        BusinessModelClient modelClient = mock(BusinessModelClient.class);
        when(modelClient.extractSceneSkillPrefill(any(SceneSkillPrefillContext.class)))
                .thenReturn(new BusinessModelResult<>(suggestion,
                        new BusinessModelCallMetadata("LIVE", "deepseek-chat", DIGEST, DIGEST, 120, 8)));
        return modelClient;
    }

    private static List<EmsDatabaseTableMetadata> tables() {
        return List.of(
                new EmsDatabaseTableMetadata("basedata", "employee_info", ""),
                new EmsDatabaseTableMetadata("basedata", "oper_log", ""),
                new EmsDatabaseTableMetadata("activiti_ems", "task_tx", ""));
    }
}
