package cn.com.crc.ems.finance.agent.harness.infrastructure;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.agentscope.core.formatter.ResponseFormat;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.TextBlock;
import io.agentscope.core.model.ChatResponse;

import cn.com.crc.ems.finance.agent.harness.application.BusinessModelCallMetadata;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelException;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisContext;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanContext;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanSuggestion;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;

/** 复用 AgentScope DeepSeek 适配器完成两个严格 JSON 业务模型用例。 */
public final class DeepSeekBusinessModelClient implements BusinessModelClient {

    private static final Duration TIMEOUT = Duration.ofSeconds(20);
    private final DeepSeekOpenAiAdapter adapter;

    public DeepSeekBusinessModelClient(DeepSeekOpenAiAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public BusinessModelResult<ModelPlanSuggestion> generatePlan(ModelPlanContext context) {
        ObjectNode input = planInput(context);
        String instruction = "你是财务共享运维调查助手。仅依据输入 JSON 生成调查方案，不执行工具。"
                + "只返回 JSON 对象，字段必须且只能是 recommendedGoal、alternativeGoals、reason；"
                + "alternativeGoals 最多两个，所有内容使用简体中文。输入："
                + CanonicalJsonCodec.canonicalize(input);
        return invoke(instruction, input, ModelPlanSuggestion::fromJson);
    }

    @Override
    public BusinessModelResult<EvidenceAnalysisSuggestion> analyzeEvidence(EvidenceAnalysisContext context) {
        ObjectNode input = evidenceInput(context);
        String instruction = "你是财务共享运维证据分析助手。仅依据脱敏摘要给出业务建议，不补造事实。"
                + "只返回 JSON 对象，字段必须且只能是 summary、riskLevel、riskExplanation、recommendations；"
                + "riskLevel 只能是 LOW、MEDIUM、HIGH，recommendations 为 1 到 8 条简体中文建议。输入："
                + CanonicalJsonCodec.canonicalize(input);
        return invoke(instruction, input, EvidenceAnalysisSuggestion::fromJson);
    }

    private <T> BusinessModelResult<T> invoke(String instruction, ObjectNode input,
            java.util.function.Function<JsonNode, T> parser) {
        long started = System.nanoTime();
        try {
            ChatResponse response = adapter.stream(List.of(Msg.builder().role(MsgRole.USER)
                            .textContent(instruction).build()), List.of(),
                    adapter.options(TIMEOUT, ResponseFormat.jsonObject(), null, 1600)).blockLast(TIMEOUT.plusSeconds(2));
            if (response == null) {
                throw new BusinessModelException("模型暂时没有返回结果，请稍后重试");
            }
            String text = response.getContent().stream().filter(TextBlock.class::isInstance)
                    .map(TextBlock.class::cast).map(TextBlock::getText).collect(Collectors.joining());
            JsonNode output = CanonicalJsonCodec.parse(text);
            T value = parser.apply(output);
            int tokens = response.getUsage() == null ? 0 : response.getUsage().getTotalTokens();
            return new BusinessModelResult<>(value, new BusinessModelCallMetadata("LIVE", adapter.modelName(),
                    Sha256Digest.hex(CanonicalJsonCodec.canonicalize(input)),
                    Sha256Digest.hex(CanonicalJsonCodec.canonicalize(output)), tokens,
                    Duration.ofNanos(System.nanoTime() - started).toMillis()));
        } catch (BusinessModelException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new BusinessModelException("模型分析暂时不可用，请稍后重试", exception);
        }
    }

    private static ObjectNode planInput(ModelPlanContext context) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("title", context.title());
        node.put("question", context.question());
        node.put("systemCode", context.systemCode());
        node.set("objectSelector", CanonicalJsonCodec.parse(context.objectSelector()));
        node.set("effectiveTimeRange", CanonicalJsonCodec.parse(context.effectiveTimeRange()));
        node.set("includedPaths", CanonicalJsonCodec.parse(context.includedPaths()));
        return node;
    }

    private static ObjectNode evidenceInput(EvidenceAnalysisContext context) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("title", context.title());
        node.put("question", context.question());
        node.put("currentCount", context.currentCount());
        node.put("futureCount", context.futureCount());
        node.put("undeterminedCount", context.undeterminedCount());
        node.putPOJO("coverageStates", context.coverageStates());
        node.putPOJO("findingStatements", context.findingStatements());
        return node;
    }
}
