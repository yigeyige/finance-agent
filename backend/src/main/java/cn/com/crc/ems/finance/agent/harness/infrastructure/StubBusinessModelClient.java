package cn.com.crc.ems.finance.agent.harness.infrastructure;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.com.crc.ems.finance.agent.harness.application.BusinessModelCallMetadata;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.BusinessHandlingDraft;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisContext;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanContext;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanSuggestion;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;

/**
 *
 * @Description: 本地流程验证使用的确定性业务模型，响应始终显式标识为 Stub。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public final class StubBusinessModelClient implements BusinessModelClient {

    private static final String MODEL_NAME = "本地模拟模型";

    /**
     *
     * @description: 返回确定性业务处理初稿或基于管理员意见的可验证修订结果。
     *
     * @param: context 已受限的业务处理上下文
     * @return: 明确标识 Stub 的结构化业务建议
     * @author: yigeyige99
     */
    @Override
    public BusinessModelResult<BusinessGuidanceSuggestion> guideBusinessHandling(BusinessGuidanceContext context) {
        long started = System.nanoTime();
        BusinessHandlingDraft draft = context.currentDraft() == null ? initialDraft(context) : revisedDraft(context);
        String reply = context.feedback() == null
                ? "我已根据业务说明整理出对象、范围和业务方案初稿，请结合实际情况调整后确认。"
                : "已根据你的修改意见更新业务处理初稿，请核对对象、范围和业务方案。";
        var value = new BusinessGuidanceSuggestion(reply, draft);
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("reply", reply);
        output.set("draft", draft.toJson());
        return result(contextJson(context), output, value, started);
    }

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

    private static BusinessHandlingDraft initialDraft(BusinessGuidanceContext context) {
        boolean adjust = context.description().contains("调整") || context.description().contains("修改")
                || context.title().contains("调整");
        String handlingType = adjust ? "ADJUST" : "INVESTIGATE";
        String action = adjust ? "梳理调整影响范围并形成可执行处理方案" : "核对已授权范围内的现状并形成可复核结论";
        return new BusinessHandlingDraft(handlingType, adjust ? "业务调整" : "业务核查", "待管理员确认的业务对象",
                context.description(), "管理员填写的业务说明", "FIN-SYN", "待管理员确认的系统对象", "2026-01-01",
                "2026-12-31", List.of("APPROVAL", "ROLE"), "MINIMUM", action,
                List.of("复核对象关联关系和责任归属", "核对时间范围内的变更及待处理事项"),
                "本地模拟模型已根据业务说明形成初稿，管理员确认后才会写入业务范围。",
                "本地模拟模型未查询 EMS 数据库，暂无检查结论。", List.of());
    }

    private static BusinessHandlingDraft revisedDraft(BusinessGuidanceContext context) {
        BusinessHandlingDraft current = context.currentDraft();
        boolean adjust = context.feedback().contains("调整") || context.feedback().contains("修改")
                || current.handlingType().equals("ADJUST");
        String goal = current.recommendedGoal() + "；已纳入管理员意见：" + context.feedback();
        return new BusinessHandlingDraft(adjust ? "ADJUST" : "INVESTIGATE", current.premiseType(),
                current.subjectKey(), current.statement(), current.sourceDescription(), current.systemCode(),
                current.objectKey(), current.startDate(), current.endDate(), current.investigationAreas(),
                current.dataUse(), goal.length() > 2000 ? goal.substring(0, 2000) : goal,
                current.alternativeGoals(), "本地模拟模型已根据管理员的本次意见更新初稿，请再次核对。",
                current.conclusion(), current.evidence());
    }

    private static ObjectNode contextJson(BusinessGuidanceContext context) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("taskId", context.taskId().toString());
        node.put("title", context.title());
        node.put("description", context.description());
        if (context.sceneSkill() != null) {
            ObjectNode skill = node.putObject("sceneSkill");
            skill.put("name", context.sceneSkill().name());
            skill.put("investigationPrompt", context.sceneSkill().investigationPrompt());
            skill.put("decisionPacketPrompt", context.sceneSkill().decisionPacketPrompt());
        }
        if (context.feedback() != null) {
            node.put("feedback", context.feedback());
            node.set("currentDraft", context.currentDraft().toJson());
        }
        if (!context.attachments().isEmpty()) {
            var attachments = node.putArray("attachments");
            context.attachments().forEach(attachment -> {
                ObjectNode item = attachments.addObject();
                item.put("fileName", attachment.fileName());
                item.put("content", attachment.content());
                item.put("truncated", attachment.truncated());
            });
        }
        if (!context.databaseEvidence().isEmpty()) node.putPOJO("databaseEvidence", context.databaseEvidence());
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
