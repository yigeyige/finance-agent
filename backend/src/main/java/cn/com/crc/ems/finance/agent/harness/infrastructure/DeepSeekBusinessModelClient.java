package cn.com.crc.ems.finance.agent.harness.infrastructure;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.agentscope.core.formatter.ResponseFormat;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.TextBlock;
import io.agentscope.core.model.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.crc.ems.finance.agent.harness.application.BusinessModelCallMetadata;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelException;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisContext;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseEvidence;
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseQueryRequest;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanContext;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.PromptTemplate;
import cn.com.crc.ems.finance.agent.harness.application.PromptTemplateRepository;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseColumnMetadata;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseTableMetadata;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;

/**
 *
 * @Description: 复用 AgentScope DeepSeek 适配器完成两个严格 JSON 业务模型用例。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public final class DeepSeekBusinessModelClient implements BusinessModelClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeepSeekBusinessModelClient.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(180);
    private final DeepSeekOpenAiAdapter adapter;
    private final PromptTemplateRepository promptTemplates;

    public DeepSeekBusinessModelClient(DeepSeekOpenAiAdapter adapter, PromptTemplateRepository promptTemplates) {
        this.adapter = adapter;
        this.promptTemplates = promptTemplates;
    }

    /** 从提示词模板读取正文并拼接运行时输入，模板缺失时快速失败。 */
    private String instruction(String templateCode, ObjectNode input) {
        return promptTemplates.findByCode(templateCode).map(PromptTemplate::content)
                .orElseThrow(() -> new IllegalStateException("prompt template missing: " + templateCode))
                + CanonicalJsonCodec.canonicalize(input);
    }

    /**
     *
     * @description: 调用真实模型生成或按管理员反馈修订严格业务处理草稿。
     *
     * @param: context 后端构造的受限业务处理上下文
     * @return: 结构化模型草稿及脱敏调用元数据
     * @author: yigeyige99
     */
    @Override
    public List<String> selectRelevantTables(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog) {
        ObjectNode input = databaseQueryInput(context);
        input.set("tableCatalog", catalogJson(tableCatalog));
        String instruction = instruction("SELECT_TABLES", input);
        try {
            ChatResponse response = adapter.stream(List.of(Msg.builder().role(MsgRole.USER).textContent(instruction).build()),
                    List.of(), adapter.options(TIMEOUT, ResponseFormat.jsonObject(), null, 256000)).blockLast(TIMEOUT.plusSeconds(2));
            if (response == null) return List.of();
            String text = response.getContent().stream().filter(TextBlock.class::isInstance)
                    .map(TextBlock.class::cast).map(TextBlock::getText).collect(Collectors.joining());
            LOGGER.info("选表返回 finishReason={} content={}", response.getFinishReason(),
                    text.substring(0, Math.min(300, text.length())));
            JsonNode tables = CanonicalJsonCodec.parse(text).get("tables");
            if (tables == null || !tables.isArray()) return List.of();
            List<String> result = new ArrayList<>();
            tables.forEach(item -> { if (item.isTextual() && item.textValue().contains(".")) result.add(item.textValue()); });
            return result.stream().distinct().limit(5).toList();
        } catch (RuntimeException exception) {
            return List.of();
        }
    }

    @Override
    public Optional<EmsDatabaseQueryRequest> planEmsDatabaseQuery(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog, List<EmsDatabaseEvidence> priorEvidence,
            Map<String, List<EmsDatabaseColumnMetadata>> tableColumns) {
        ObjectNode input = databaseQueryInput(context);
        if (tableColumns != null && !tableColumns.isEmpty()) {
            input.set("tableColumns", columnsJson(tableColumns));
        } else if (tableCatalog != null && !tableCatalog.isEmpty()) {
            input.set("tableCatalog", catalogJson(tableCatalog));
        }
        if (priorEvidence != null && !priorEvidence.isEmpty()) {
            input.set("priorEvidence", evidenceJson(priorEvidence));
        }
        String instruction = instruction("PLAN_QUERY", input);
        try {
            ChatResponse response = adapter.stream(List.of(Msg.builder().role(MsgRole.USER).textContent(instruction).build()), List.of(),
                    adapter.options(TIMEOUT, ResponseFormat.jsonObject(), null, 256000)).blockLast(TIMEOUT.plusSeconds(2));
            if (response == null) return Optional.empty();
            String text = response.getContent().stream().filter(TextBlock.class::isInstance)
                    .map(TextBlock.class::cast).map(TextBlock::getText).collect(Collectors.joining());
            LOGGER.info("查询规划返回 finishReason={} content={}", response.getFinishReason(),
                    text.substring(0, Math.min(300, text.length())));
            return EmsDatabaseQueryRequest.fromJson(CanonicalJsonCodec.parse(text));
        } catch (RuntimeException exception) {
            return Optional.empty();
        }
    }

    private static JsonNode columnsJson(Map<String, List<EmsDatabaseColumnMetadata>> tableColumns) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        tableColumns.forEach((table, columns) -> {
            ArrayNode array = node.putArray(table);
            columns.stream().limit(20).forEach(column -> {
                ObjectNode item = array.addObject();
                item.put("name", column.name());
                if (column.comment() != null && !column.comment().isBlank()) {
                    item.put("comment", column.comment());
                }
            });
        });
        return node;
    }

    private static JsonNode catalogJson(List<EmsDatabaseTableMetadata> catalog) {
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        catalog.forEach(item -> {
            ObjectNode node = array.addObject();
            node.put("schema", item.schema());
            node.put("table", item.table());
            node.put("comment", item.comment());
        });
        return array;
    }

    private static JsonNode evidenceJson(List<EmsDatabaseEvidence> evidence) {
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        evidence.stream().filter(item -> !item.rows().isEmpty()).forEach(item -> {
            ObjectNode node = array.addObject();
            node.put("schema", item.schema());
            node.put("table", item.table());
            node.put("purpose", item.purpose());
            node.putPOJO("columns", item.columns());
            node.putPOJO("rows", item.rows().stream().limit(20).toList());
        });
        return array;
    }

    @Override
    public BusinessModelResult<BusinessGuidanceSuggestion> guideBusinessHandling(BusinessGuidanceContext context) {
        ObjectNode input = guidanceInput(context);
        String instruction = instruction("GUIDE_HANDLING", input);
        return invoke(instruction, input, BusinessGuidanceSuggestion::fromJson);
    }

    @Override
    public BusinessModelResult<ModelPlanSuggestion> generatePlan(ModelPlanContext context) {
        ObjectNode input = planInput(context);
        String instruction = instruction("GENERATE_PLAN", input);
        return invoke(instruction, input, ModelPlanSuggestion::fromJson);
    }

    @Override
    public BusinessModelResult<EvidenceAnalysisSuggestion> analyzeEvidence(EvidenceAnalysisContext context) {
        ObjectNode input = evidenceInput(context);
        String instruction = instruction("ANALYZE_EVIDENCE", input);
        return invoke(instruction, input, EvidenceAnalysisSuggestion::fromJson);
    }

    private <T> BusinessModelResult<T> invoke(String instruction, ObjectNode input,
            java.util.function.Function<JsonNode, T> parser) {
        long started = System.nanoTime();
        try {
            ChatResponse response = adapter.stream(List.of(Msg.builder().role(MsgRole.USER)
                            .textContent(instruction).build()), List.of(),
                    adapter.options(TIMEOUT, ResponseFormat.jsonObject(), null, 256000)).blockLast(TIMEOUT.plusSeconds(2));
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
            LOGGER.warn("业务模型输出不符合契约: {}", exception.getMessage());
            throw exception;
        } catch (RuntimeException exception) {
            LOGGER.warn("业务模型调用失败: {}", describe(exception));
            throw new BusinessModelException("模型分析暂时不可用，请稍后重试", exception);
        }
    }

    /** 仅记录异常类名与脱敏消息链，不记录提示词、密钥或模型输出。 */
    private static String describe(Throwable exception) {
        StringBuilder builder = new StringBuilder(exception.getClass().getSimpleName());
        if (exception.getMessage() != null) {
            builder.append(": ").append(exception.getMessage());
        }
        Throwable current = exception.getCause();
        while (current != null && current.getCause() != current) {
            builder.append(" <- ").append(current.getClass().getSimpleName());
            if (current.getMessage() != null) {
                builder.append(": ").append(current.getMessage());
            }
            current = current.getCause();
        }
        return builder.toString();
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

    private static ObjectNode guidanceInput(BusinessGuidanceContext context) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("title", context.title());
        node.put("description", context.description());
        if (context.sceneSkill() != null) {
            ObjectNode skill = node.putObject("sceneSkill");
            skill.put("name", context.sceneSkill().name());
            skill.put("investigationPrompt", context.sceneSkill().investigationPrompt());
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
        if (!context.databaseEvidence().isEmpty()) {
            var evidence = node.putArray("databaseEvidence");
            context.databaseEvidence().forEach(item -> {
                ObjectNode projected = evidence.addObject();
                projected.put("schema", item.schema());
                projected.put("table", item.table());
                projected.put("purpose", item.purpose());
                projected.putPOJO("columns", item.columns());
                projected.putPOJO("rows", item.rows());
                projected.put("boundary", "只读查询的待核验辅助信息，不代表已确认事实或授权执行操作");
            });
        }
        return node;
    }

    private static ObjectNode databaseQueryInput(BusinessGuidanceContext context) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("title", context.title());
        node.put("description", context.description());
        if (context.feedback() != null) node.put("feedback", context.feedback());
        if (context.currentDraft() != null) node.set("currentDraft", context.currentDraft().toJson());
        if (context.sceneSkill() != null) {
            ObjectNode skill = node.putObject("sceneSkill");
            skill.put("name", context.sceneSkill().name());
            skill.put("investigationPrompt", context.sceneSkill().investigationPrompt());
        }
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
