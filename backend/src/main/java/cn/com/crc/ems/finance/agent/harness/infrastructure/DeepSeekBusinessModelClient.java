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

import cn.com.crc.ems.finance.agent.harness.application.SceneSkillPrefillContext;
import cn.com.crc.ems.finance.agent.harness.application.SceneSkillPrefillSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.BusinessIntakeContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessIntakeOption;
import cn.com.crc.ems.finance.agent.harness.application.BusinessIntakeSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelCall;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelCallMetadata;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelException;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.harness.application.BusinessPacketAnalysisContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessPacketAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisContext;
import cn.com.crc.ems.finance.agent.harness.application.EvidenceAnalysisSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseEvidence;
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseQueryPlan;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanContext;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.PromptTemplate;
import cn.com.crc.ems.finance.agent.harness.application.PromptTemplateRepository;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseColumnMetadata;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseTableMetadata;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;
import cn.com.crc.ems.finance.agent.processartifact.domain.ModelInputBudget;
import cn.com.crc.ems.finance.agent.processartifact.application.ProcessArtifactModelContext;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactPlan;

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
    /** 与前端模型请求超时一致：大输入/复杂流程下允许更长的推理与输出时间。 */
    private static final Duration TIMEOUT = Duration.ofSeconds(600);
    /** AI 预填是交互前置动作，使用更短的独立超时。 */
    private static final Duration INTAKE_TIMEOUT = Duration.ofSeconds(60);
    /** 场景预填同样是交互前置动作，独立超时不影响任务编排调用。 */
    private static final Duration SCENE_PREFILL_TIMEOUT = Duration.ofSeconds(60);
    /**
     * 所有模型调用统一使用 128K 输出上限：选表/查询规划、初稿、决策包、流程文件生成都不再使用 16K。
     * deepseek-flash 官方最大输出 384K；128K 是当前统一的安全默认值，必要时可继续提高。
     */
    private static final int DEFAULT_MAX_TOKENS = 131_072;
    private final DeepSeekOpenAiAdapter adapter;
    private final PromptTemplateRepository promptTemplates;

    public DeepSeekBusinessModelClient(DeepSeekOpenAiAdapter adapter, PromptTemplateRepository promptTemplates) {
        this.adapter = adapter;
        this.promptTemplates = promptTemplates;
    }

    /** 从提示词模板读取正文并拼接运行时输入，模板缺失时快速失败。 */
    private String instruction(String templateCode, ObjectNode input) {
        String instruction = promptTemplates.findByCode(templateCode).map(PromptTemplate::content)
                .orElseThrow(() -> new IllegalStateException("prompt template missing: " + templateCode))
                + CanonicalJsonCodec.canonicalize(input);
        ModelInputBudget.requireWithinLimit(adapter.modelName(), instruction);
        return instruction;
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
    public BusinessModelCall<List<String>> selectRelevantTables(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog) {
        return selectRelevantTables(context, tableCatalog, 5);
    }

    @Override
    public BusinessModelCall<List<String>> selectRelevantTables(BusinessGuidanceContext context,
            List<EmsDatabaseTableMetadata> tableCatalog, int maxTables) {
        ObjectNode input = databaseQueryInput(context);
        input.set("tableCatalog", catalogJson(tableCatalog));
        input.put("maxTables", maxTables);
        String instruction = instruction("SELECT_TABLES", input);
        return invokeOptional(instruction, input, node -> {
            JsonNode tables = node.get("tables");
            if (tables == null || !tables.isArray()) return List.<String>of();
            List<String> result = new ArrayList<>();
            tables.forEach(item -> { if (item.isTextual() && item.textValue().contains(".")) result.add(item.textValue()); });
            return result.stream().distinct().limit(maxTables).toList();
        }, List.of());
    }

    @Override
    public BusinessModelCall<EmsDatabaseQueryPlan> planEmsDatabaseQuery(BusinessGuidanceContext context,
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
        return invokeOptional(instruction, input, EmsDatabaseQueryPlan::fromJson, new EmsDatabaseQueryPlan(List.of()));
    }

    /**
     *
     * @description: 选表与查询规划允许失败降级：失败时返回兜底值，同时保留可计量的调用元数据。
     *
     * @param: instruction 已拼接的提示词
     * @param: input 规范化输入，仅用于生成摘要
     * @param: parser 输出解析器
     * @param: fallback 失败或空响应时的兜底值
     * @return: 业务值与调用元数据
     * @author: yigeyige99
     */
    private <T> BusinessModelCall<T> invokeOptional(String instruction, ObjectNode input,
            java.util.function.Function<JsonNode, T> parser, T fallback) {
        long started = System.nanoTime();
        try {
            ChatResponse response = adapter.stream(List.of(Msg.builder().role(MsgRole.USER).textContent(instruction).build()),
                    List.of(), adapter.options(TIMEOUT, ResponseFormat.jsonObject(), null, DEFAULT_MAX_TOKENS))
                    .blockLast(TIMEOUT.plusSeconds(2));
            if (response == null) {
                return BusinessModelCall.failed(fallback, adapter.modelName(), elapsedMillis(started));
            }
            String text = response.getContent().stream().filter(TextBlock.class::isInstance)
                    .map(TextBlock.class::cast).map(TextBlock::getText).collect(Collectors.joining());
            LOGGER.info("业务模型可选调用返回 finishReason={}, chars={}", response.getFinishReason(), text.length());
            JsonNode output = CanonicalJsonCodec.parse(text);
            T value = parser.apply(output);
            return new BusinessModelCall<>(value, new BusinessModelCallMetadata("LIVE", adapter.modelName(),
                    Sha256Digest.hex(CanonicalJsonCodec.canonicalize(input)),
                    Sha256Digest.hex(CanonicalJsonCodec.canonicalize(output)),
                    response.getUsage() == null ? 0 : response.getUsage().getTotalTokens(),
                    elapsedMillis(started)));
        } catch (RuntimeException exception) {
            LOGGER.warn("业务模型可选调用失败: {}", describe(exception));
            return BusinessModelCall.failed(fallback, adapter.modelName(), elapsedMillis(started));
        }
    }

    private static long elapsedMillis(long startedNanos) {
        return Duration.ofNanos(System.nanoTime() - startedNanos).toMillis();
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
        List<String> warnings = new ArrayList<>();
        return invokeWithWarnings(instruction, input,
                node -> new ParsedModelOutput<>(BusinessGuidanceSuggestion.fromJson(node, warnings), warnings));
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

    @Override
    public BusinessModelResult<BusinessPacketAnalysisSuggestion> analyzeDecisionPacket(
            BusinessPacketAnalysisContext context) {
        ObjectNode input = packetInput(context);
        String instruction = instruction("ANALYZE_DECISION_PACKET", input);
        return invoke(instruction, input, BusinessPacketAnalysisSuggestion::fromJson);
    }

    @Override
    public BusinessModelResult<ProcessArtifactPlan> generateProcessArtifacts(ProcessArtifactModelContext context) {
        ObjectNode input = context.toInputJson();
        String instruction = context.prompt() + CanonicalJsonCodec.canonicalize(input);
        ModelInputBudget.requireWithinLimit(adapter.modelName(), instruction);
        return invoke(instruction, input, output -> {
            try {
                return ProcessArtifactPlan.fromJson(output);
            } catch (IllegalArgumentException exception) {
                throw new BusinessModelException("模型生成的流程结构不符合约定，请调整调查结论后重试", exception);
            }
        });
    }

    @Override
    public BusinessModelResult<BusinessIntakeSuggestion> extractBusinessIntake(BusinessIntakeContext context) {
        ObjectNode input = JsonNodeFactory.instance.objectNode();
        input.put("text", context.text());
        input.set("sceneSkills", optionsJson(context.sceneSkills()));
        input.set("databaseInstances", optionsJson(context.databaseInstances()));
        String instruction = instruction("EXTRACT_BUSINESS_INTAKE", input);
        return invokeIntake(instruction, input, node -> new BusinessIntakeSuggestion(
                textValue(node, "title"), textValue(node, "description"),
                textValue(node, "sceneSkillName"), textValue(node, "databaseName")));
    }

    /** 候选只传名称，避免把内部标识暴露给模型。 */
    @Override
    public BusinessModelResult<SceneSkillPrefillSuggestion> extractSceneSkillPrefill(SceneSkillPrefillContext context) {
        ObjectNode input = JsonNodeFactory.instance.objectNode();
        input.put("target", context.target().name());
        input.put("text", context.text());
        input.put("sceneName", context.sceneName());
        input.put("prerequisitePrompt", context.prerequisitePrompt());
        var tables = input.putArray("tableNames");
        context.tableNames().forEach(tables::add);
        String instruction = instruction("SCENE_SKILL_PREFILL", input);
        return invokeScenePrefill(instruction, input,
                node -> new SceneSkillPrefillSuggestion(textValue(node, "name"), textValue(node, "content")));
    }

    private static com.fasterxml.jackson.databind.node.ArrayNode optionsJson(List<BusinessIntakeOption> options) {        var array = JsonNodeFactory.instance.arrayNode();
        options.forEach(option -> array.add(option.name()));
        return array;
    }

    private static String textValue(JsonNode node, String field) {
        JsonNode value = node == null ? null : node.get(field);
        return value == null || value.isNull() ? "" : value.asText("");
    }

    /** AI 预填专用调用：独立 60s 超时与超时语义，不复用任务编排的 invokeInternal。 */
    private <T> BusinessModelResult<T> invokeIntake(String instruction, ObjectNode input,
            java.util.function.Function<JsonNode, T> parser) {
        long started = System.nanoTime();
        try {
            ChatResponse response = adapter.stream(List.of(Msg.builder().role(MsgRole.USER)
                            .textContent(instruction).build()), List.of(),
                    adapter.options(INTAKE_TIMEOUT, ResponseFormat.jsonObject(), null, DEFAULT_MAX_TOKENS))
                    .blockLast(INTAKE_TIMEOUT.plusSeconds(2));
            if (response == null) {
                throw new BusinessModelException("模型暂时没有返回结果，请稍后重试");
            }
            String text = response.getContent().stream().filter(TextBlock.class::isInstance)
                    .map(TextBlock.class::cast).map(TextBlock::getText).collect(Collectors.joining());
            JsonNode output;
            try {
                output = CanonicalJsonCodec.parse(text);
            } catch (RuntimeException parseException) {
                LOGGER.warn("AI 预填输出解析失败: finishReason={}, chars={}, {}",
                        response.getFinishReason(), text.length(), parseException.getMessage());
                throw parseException;
            }
            T value = parser.apply(output);
            int tokens = response.getUsage() == null ? 0 : response.getUsage().getTotalTokens();
            return new BusinessModelResult<>(value, new BusinessModelCallMetadata("LIVE", adapter.modelName(),
                    Sha256Digest.hex(CanonicalJsonCodec.canonicalize(input)),
                    Sha256Digest.hex(CanonicalJsonCodec.canonicalize(output)), tokens,
                    Duration.ofNanos(System.nanoTime() - started).toMillis()));
        } catch (BusinessModelException exception) {
            LOGGER.warn("AI 预填输出不符合契约: {}", describe(exception));
            throw exception;
        } catch (RuntimeException exception) {
            LOGGER.warn("AI 预填调用失败: {}", describe(exception));
            if (isTimeout(exception)) {
                throw new cn.com.crc.ems.finance.agent.harness.application.BusinessModelTimeoutException(
                        "模型响应超时，请稍后重试", exception);
            }
            throw new BusinessModelException("模型分析暂时不可用，请稍后重试", exception);
        }
    }

    private <T> BusinessModelResult<T> invoke(String instruction, ObjectNode input,
            java.util.function.Function<JsonNode, T> parser) {
        return invokeInternal(instruction, input,
                node -> new ParsedModelOutput<>(parser.apply(node), List.of()));
    }

    private <T> BusinessModelResult<T> invokeWithWarnings(String instruction, ObjectNode input,
            java.util.function.Function<JsonNode, ParsedModelOutput<T>> parser) {
        return invokeInternal(instruction, input, parser);
    }

    private <T> BusinessModelResult<T> invokeInternal(String instruction, ObjectNode input,
            java.util.function.Function<JsonNode, ParsedModelOutput<T>> parser) {
        long started = System.nanoTime();
        try {
            ChatResponse response = adapter.stream(List.of(Msg.builder().role(MsgRole.USER)
                            .textContent(instruction).build()), List.of(),
                    adapter.options(TIMEOUT, ResponseFormat.jsonObject(), null, DEFAULT_MAX_TOKENS))
                    .blockLast(TIMEOUT.plusSeconds(2));
            if (response == null) {
                throw new BusinessModelException("模型暂时没有返回结果，请稍后重试");
            }
            String text = response.getContent().stream().filter(TextBlock.class::isInstance)
                    .map(TextBlock.class::cast).map(TextBlock::getText).collect(Collectors.joining());
            JsonNode output;
            try {
                output = CanonicalJsonCodec.parse(text);
            } catch (RuntimeException parseException) {
                // 截断时 finishReason 通常为 length；只记录元信息，不记录提示词或模型内容。
                LOGGER.warn("业务模型输出解析失败: finishReason={}, chars={}, {}",
                        response.getFinishReason(), text.length(), parseException.getMessage());
                throw parseException;
            }
            ParsedModelOutput<T> parsed = parser.apply(output);
            int tokens = response.getUsage() == null ? 0 : response.getUsage().getTotalTokens();
            return new BusinessModelResult<>(parsed.value(), new BusinessModelCallMetadata("LIVE", adapter.modelName(),
                    Sha256Digest.hex(CanonicalJsonCodec.canonicalize(input)),
                    Sha256Digest.hex(CanonicalJsonCodec.canonicalize(output)), tokens,
                    Duration.ofNanos(System.nanoTime() - started).toMillis()), parsed.warnings());
        } catch (BusinessModelException exception) {
            LOGGER.warn("业务模型输出不符合契约: {}", describe(exception));
            throw exception;
        } catch (RuntimeException exception) {
            LOGGER.warn("业务模型调用失败: {}", describe(exception));
            throw new BusinessModelException("模型分析暂时不可用，请稍后重试", exception);
        }
    }

    /** 场景预填专用调用：独立超时与日志语义，不复用任务编排与 intake 的调用方法。 */
    private <T> BusinessModelResult<T> invokeScenePrefill(String instruction, ObjectNode input,
            java.util.function.Function<JsonNode, T> parser) {
        long started = System.nanoTime();
        try {
            ChatResponse response = adapter.stream(List.of(Msg.builder().role(MsgRole.USER)
                            .textContent(instruction).build()), List.of(),
                    adapter.options(SCENE_PREFILL_TIMEOUT, ResponseFormat.jsonObject(), null, DEFAULT_MAX_TOKENS))
                    .blockLast(SCENE_PREFILL_TIMEOUT.plusSeconds(2));
            if (response == null) {
                throw new BusinessModelException("模型暂时没有返回结果，请稍后重试");
            }
            String text = response.getContent().stream().filter(TextBlock.class::isInstance)
                    .map(TextBlock.class::cast).map(TextBlock::getText).collect(Collectors.joining());
            JsonNode output;
            try {
                output = CanonicalJsonCodec.parse(text);
            } catch (RuntimeException parseException) {
                LOGGER.warn("场景预填输出解析失败: finishReason={}, chars={}, {}",
                        response.getFinishReason(), text.length(), parseException.getMessage());
                throw parseException;
            }
            T value = parser.apply(output);
            int tokens = response.getUsage() == null ? 0 : response.getUsage().getTotalTokens();
            return new BusinessModelResult<>(value, new BusinessModelCallMetadata("LIVE", adapter.modelName(),
                    Sha256Digest.hex(CanonicalJsonCodec.canonicalize(input)),
                    Sha256Digest.hex(CanonicalJsonCodec.canonicalize(output)), tokens,
                    Duration.ofNanos(System.nanoTime() - started).toMillis()));
        } catch (BusinessModelException exception) {
            LOGGER.warn("场景预填输出不符合契约: {}", describe(exception));
            throw exception;
        } catch (RuntimeException exception) {
            LOGGER.warn("场景预填调用失败: {}", describe(exception));
            if (isTimeout(exception)) {
                throw new cn.com.crc.ems.finance.agent.harness.application.BusinessModelTimeoutException(
                        "模型响应超时，请稍后重试", exception);
            }
            throw new BusinessModelException("模型分析暂时不可用，请稍后重试", exception);
        }
    }

    /** 判断异常链中是否包含阻塞超时，用于映射 504 而不是普通失败。 */
    private static boolean isTimeout(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof java.util.concurrent.TimeoutException) return true;
            current = current.getCause() == current ? null : current.getCause();
        }
        return false;
    }

    private record ParsedModelOutput<T>(T value, List<String> warnings) {
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
        node.set("objectSelector", CanonicalJsonCodec.parse(context.objectSelector()));
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
        if (!context.sceneAttachmentCatalog().isEmpty()) {
            var catalog = node.putArray("sceneAttachmentCatalog");
            context.sceneAttachmentCatalog().forEach(item -> {
                ObjectNode projected = catalog.addObject();
                projected.put("fileName", item.fileName());
                projected.put("summary", item.summary());
            });
        }
        if (!context.sceneAttachmentBodies().isEmpty()) {
            var bodies = node.putArray("sceneAttachmentBodies");
            context.sceneAttachmentBodies().forEach(item -> {
                ObjectNode projected = bodies.addObject();
                projected.put("fileName", item.fileName());
                projected.put("content", item.content());
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

    private static ObjectNode packetInput(BusinessPacketAnalysisContext context) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("title", context.title());
        node.put("question", context.question());
        if (context.decisionPacketPrompt() != null && !context.decisionPacketPrompt().isBlank()) {
            node.put("decisionPacketPrompt", context.decisionPacketPrompt());
        }
        node.set("evidence", evidenceJson(context.evidence()));
        node.set("guidanceDraft", context.guidanceDraft().toJson());
        return node;
    }
}
