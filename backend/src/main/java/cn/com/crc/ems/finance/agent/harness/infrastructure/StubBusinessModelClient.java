package cn.com.crc.ems.finance.agent.harness.infrastructure;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.com.crc.ems.finance.agent.harness.application.BusinessModelCallMetadata;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisContext;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanContext;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanSuggestion;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;

/** 本地流程验证使用的确定性业务模型，响应始终显式标识为 Stub。 */
public final class StubBusinessModelClient implements BusinessModelClient {

    private static final String MODEL_NAME = "本地模拟模型";

    @Override
    public BusinessModelResult<ModelPlanSuggestion> generatePlan(ModelPlanContext context) {
        long started = System.nanoTime();
        List<String> alternatives = new ArrayList<>();
        alternatives.add("核对已确认范围内的对象状态、责任关系和异常引用");
        alternatives.add("复核时间范围内的变更记录并定位仍待确认的事项");
        var value = new ModelPlanSuggestion("围绕“" + context.title() + "”汇总授权证据并形成可复核结论",
                alternatives, "本地模拟模型根据任务问题和已确认范围生成，用于验证模型交互流程，执行前仍需人工批准。");
        return result(contextJson(context), planJson(value), value, started);
    }

    @Override
    public BusinessModelResult<EvidenceAnalysisSuggestion> analyzeEvidence(EvidenceAnalysisContext context) {
        long started = System.nanoTime();
        String risk = context.undeterminedCount() > 0 ? "HIGH"
                : context.coverageStates().stream().allMatch("COMPLETE"::equals) ? "MEDIUM" : "HIGH";
        List<String> recommendations = context.findingStatements().stream().limit(5).toList();
        if (recommendations.isEmpty()) {
            recommendations = List.of("补充调查判断后再形成决策包");
        }
        var value = new EvidenceAnalysisSuggestion(
                "已汇总 " + (context.currentCount() + context.futureCount() + context.undeterminedCount())
                        + " 条事实和 " + context.findingStatements().size() + " 条调查判断。",
                risk, context.undeterminedCount() > 0 ? "仍有待确定事实，需要在发布前补充核对。"
                        : "当前证据已形成判断，但业务影响仍需人工确认。",
                recommendations);
        return result(contextJson(context), analysisJson(value), value, started);
    }

    private static <T> BusinessModelResult<T> result(ObjectNode input, ObjectNode output, T value, long started) {
        return new BusinessModelResult<>(value, new BusinessModelCallMetadata("STUB", MODEL_NAME,
                Sha256Digest.hex(CanonicalJsonCodec.canonicalize(input)),
                Sha256Digest.hex(CanonicalJsonCodec.canonicalize(output)), 0,
                java.time.Duration.ofNanos(System.nanoTime() - started).toMillis()));
    }

    private static ObjectNode contextJson(ModelPlanContext context) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("taskId", context.taskId().toString());
        node.put("scopeRevisionId", context.scopeRevisionId().toString());
        node.put("title", context.title());
        node.put("question", context.question());
        node.put("systemCode", context.systemCode());
        node.set("objectSelector", CanonicalJsonCodec.parse(context.objectSelector()));
        node.set("effectiveTimeRange", CanonicalJsonCodec.parse(context.effectiveTimeRange()));
        node.set("includedPaths", CanonicalJsonCodec.parse(context.includedPaths()));
        return node;
    }

    private static ObjectNode contextJson(EvidenceAnalysisContext context) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("taskId", context.taskId().toString());
        node.put("title", context.title());
        node.put("question", context.question());
        node.put("currentCount", context.currentCount());
        node.put("futureCount", context.futureCount());
        node.put("undeterminedCount", context.undeterminedCount());
        node.putPOJO("coverageStates", context.coverageStates());
        node.putPOJO("findingStatements", context.findingStatements());
        return node;
    }

    private static ObjectNode planJson(ModelPlanSuggestion value) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("recommendedGoal", value.recommendedGoal());
        node.putPOJO("alternativeGoals", value.alternativeGoals());
        node.put("reason", value.reason());
        return node;
    }

    private static ObjectNode analysisJson(EvidenceAnalysisSuggestion value) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("summary", value.summary());
        node.put("riskLevel", value.riskLevel());
        node.put("riskExplanation", value.riskExplanation());
        node.putPOJO("recommendations", value.recommendations());
        return node;
    }
}
