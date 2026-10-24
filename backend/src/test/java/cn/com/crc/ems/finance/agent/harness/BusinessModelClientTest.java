package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelException;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceAttachment;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisContext;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanContext;
import cn.com.crc.ems.finance.agent.harness.application.PromptTemplate;
import cn.com.crc.ems.finance.agent.harness.application.PromptTemplateRepository;
import cn.com.crc.ems.finance.agent.harness.infrastructure.DeepSeekBusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.infrastructure.DeepSeekOpenAiAdapter;
import cn.com.crc.ems.finance.agent.harness.infrastructure.StubBusinessModelClient;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;

class BusinessModelClientTest {

    private HttpServer server;

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void stubIsExplicitAndDeterministicForBothInteractions() {
        StubBusinessModelClient client = new StubBusinessModelClient();

        var plan = client.generatePlan(planContext());
        var analysis = client.analyzeEvidence(evidenceContext());

        assertThat(plan.metadata().mode()).isEqualTo("STUB");
        assertThat(plan.metadata().modelName()).isEqualTo("本地模拟模型");
        assertThat(plan.value().alternativeGoals()).hasSize(2);
        assertThat(analysis.metadata().mode()).isEqualTo("STUB");
        assertThat(analysis.value().riskLevel()).isEqualTo("MEDIUM");
        assertThat(analysis.value().recommendations()).containsExactly("发现一条仍生效的异常引用");
    }

    @Test
    void stubGeneratesAndRevisesBusinessHandlingDraft() {
        StubBusinessModelClient client = new StubBusinessModelClient();
        var initial = client.guideBusinessHandling(new BusinessGuidanceContext(UUID.randomUUID(), "离职权限处置",
                "需要调整离职人员仍生效的审批权限", null, null));
        var revised = client.guideBusinessHandling(new BusinessGuidanceContext(UUID.randomUUID(), "离职权限处置",
                "需要调整离职人员仍生效的审批权限", "改为业务调整并优先处理审批权限", initial.value().draft()));

        assertThat(initial.metadata().mode()).isEqualTo("STUB");
        assertThat(initial.value().draft().handlingType()).isEqualTo("ADJUST");
        assertThat(initial.value().draft().investigationAreas()).containsExactly("APPROVAL", "ROLE");
        assertThat(revised.value().reply()).contains("更新");
        assertThat(revised.value().draft().recommendedGoal()).contains("管理员意见");
    }

    @Test
    void stubIncludesRestrictedAttachmentProjectionInItsInputDigest() {
        StubBusinessModelClient client = new StubBusinessModelClient();
        UUID taskId = UUID.randomUUID();
        var withoutAttachment = client.guideBusinessHandling(new BusinessGuidanceContext(taskId, "离职权限处置",
                "需要调整离职人员仍生效的审批权限", null, null));
        var withAttachment = client.guideBusinessHandling(new BusinessGuidanceContext(taskId, "离职权限处置",
                "需要调整离职人员仍生效的审批权限", null, null, null,
                List.of(new BusinessGuidanceAttachment("交接.md", "交接材料，待人工核验。", false))));

        assertThat(withAttachment.metadata().inputDigest()).isNotEqualTo(withoutAttachment.metadata().inputDigest());
        assertThat(withAttachment.value().draft().handlingType()).isEqualTo("ADJUST");
    }

    @Test
    void liveClientUsesStrictJsonAndDoesNotExposeReasoning() throws IOException {
        AtomicReference<String> request = new AtomicReference<>();
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/chat/completions", exchange -> {
            request.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            String content = "{\"recommendedGoal\":\"核对当前有效配置\",\"alternativeGoals\":[\"复核变更记录\"],\"reason\":\"先确认当前影响\"}";
            String response = "{\"id\":\"model-plan\",\"object\":\"chat.completion\",\"created\":1,"
                    + "\"model\":\"deepseek-chat\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\","
                    + "\"content\":" + quote(content) + ",\"reasoning_content\":\"private reasoning\"},\"finish_reason\":\"stop\"}],"
                    + "\"usage\":{\"prompt_tokens\":10,\"completion_tokens\":8,\"total_tokens\":18}}";
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        var settings = new FinanceAgentProperties.DeepSeek();
        settings.setApiKey("test-key");
        settings.setBaseUrl("http://localhost:" + server.getAddress().getPort());
        settings.setModel("deepseek-chat");

        PromptTemplateRepository templates = code -> Optional.of(new PromptTemplate(code,
                "你是财务共享运维调查助手。仅依据输入 JSON 生成调查方案，不执行工具。只返回 JSON 对象，字段必须且只能是 recommendedGoal、alternativeGoals、reason；alternativeGoals 最多两个，所有内容使用简体中文。输入："));
        var result = new DeepSeekBusinessModelClient(new DeepSeekOpenAiAdapter(settings), templates).generatePlan(planContext());

        assertThat(result.metadata().mode()).isEqualTo("LIVE");
        assertThat(result.metadata().modelName()).isEqualTo("deepseek-chat");
        assertThat(result.metadata().tokens()).isEqualTo(18);
        assertThat(result.value().recommendedGoal()).isEqualTo("核对当前有效配置");
        assertThat(request.get()).contains("recommendedGoal").doesNotContain("test-key");
    }

    @Test
    void schemasRejectExtraFieldsAndIncompleteEvidence() {
        assertThatThrownBy(() -> EvidenceAnalysisSuggestion.fromJson(CanonicalJsonCodec.parse(
                "{\"summary\":\"摘要\",\"riskLevel\":\"LOW\",\"riskExplanation\":\"说明\",\"recommendations\":[\"建议\"],\"extra\":true}")))
                .isInstanceOf(BusinessModelException.class);
        assertThatThrownBy(() -> new EvidenceAnalysisContext(UUID.randomUUID(), "任务", "问题", 0, 0, 0,
                List.of(), List.of())).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> BusinessGuidanceSuggestion.fromJson(CanonicalJsonCodec.parse(
                "{\"reply\":\"已生成\",\"draft\":{},\"extra\":true}")))
                .isInstanceOf(BusinessModelException.class);
    }

    private static ModelPlanContext planContext() {
        return new ModelPlanContext(UUID.randomUUID(), "离职人员配置核查", "是否仍有有效配置", UUID.randomUUID(),
                "FIN-SYN", "{\"businessObject\":\"E001\"}",
                "{\"from\":\"2026-01-01\",\"to\":\"2026-12-31\"}", "[\"workflow.approvals\"]");
    }

    private static EvidenceAnalysisContext evidenceContext() {
        return new EvidenceAnalysisContext(UUID.randomUUID(), "离职人员配置核查", "是否仍有有效配置",
                1, 0, 0, List.of("COMPLETE"), List.of("发现一条仍生效的异常引用"));
    }

    private static String quote(String value) {
        return CanonicalJsonCodec.canonicalize(CanonicalJsonCodec.parse("{\"value\":"
                + com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.textNode(value) + "}"))
                .replaceFirst("^\\{\"value\":", "").replaceFirst("}$", "");
    }
}
