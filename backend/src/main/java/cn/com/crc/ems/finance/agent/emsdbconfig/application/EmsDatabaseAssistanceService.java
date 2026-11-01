package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseEvidence;
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseQueryPlan;

/**
 *
 * @Description: 按实例结构目录或既有直连路径选表，再经只读 SQL 守卫查询实际配置并累积证据。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Service
public class EmsDatabaseAssistanceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmsDatabaseAssistanceService.class);
    private static final int MAX_ROUNDS = 3;
    private static final Set<String> FILTER_KEYWORDS = Set.of("approve", "audit", "role", "user", "org",
            "process", "flow", "config", "employee", "staff", "person", "permission", "authorize", "esb",
            "category", "claim", "value_set", "dict", "value");
    private static final Pattern FROM_SOURCE = Pattern.compile("(?i)\\bFROM\\s+`?([a-z0-9_]+)`?\\.`?([a-z0-9_]+)`?");

    private final EmsDatabaseConnectionConfigService configService;
    private final EmsReadOnlySqlGuard sqlGuard;
    private final EmsDatabaseQueryExecutor executor;
    private final EmsSchemaCatalogService catalogService;
    private final Map<UUID, CachedSelection> selectionCache = new ConcurrentHashMap<>();

    public EmsDatabaseAssistanceService(EmsDatabaseConnectionConfigService configService, EmsReadOnlySqlGuard sqlGuard,
            EmsDatabaseQueryExecutor executor, EmsSchemaCatalogService catalogService) {
        this.configService = configService;
        this.sqlGuard = sqlGuard;
        this.executor = executor;
        this.catalogService = catalogService;
    }

    /** 已同步目录未命中时明确终止分析；其他受控查询失败沿用降级行为，并回传进度文案。 */
    public List<EmsDatabaseEvidence> collect(BusinessModelClient modelClient, BusinessGuidanceContext context,
            UUID databaseConfigId, Consumer<String> progress) {
        if (databaseConfigId == null) {
            LOGGER.info("任务未选择数据库实例，跳过 EMS 辅助查询");
            return List.of();
        }
        try {
            EmsDatabaseConnectionConfig config = configService.resolveEnabled(databaseConfigId);
        Optional<EmsSchemaCatalog> snapshot = catalogService.find(databaseConfigId);
        String searchText = context.title() + " " + context.description()
                + " " + (context.feedback() == null ? "" : context.feedback());
        List<EmsSchemaTable> matches = snapshot.map(value -> value.search(searchText, 20)).orElse(List.of());
        if (snapshot.isPresent() && matches.isEmpty()) throw new EmsSchemaCatalogNoMatchException();
        List<EmsDatabaseTableMetadata> catalog = snapshot.isPresent()
                ? matches.stream().map(EmsSchemaTable::metadata).toList()
                : coarseFilter(executor.listAllowedTables(config));
        LOGGER.info("EMS 辅助查询：候选表 {} 张", catalog.size());
        if (catalog.isEmpty()) {
            return List.of();
        }
        List<String> catalogNames = catalog.stream().map(item -> item.schema() + "." + item.table()).toList();
        CachedSelection cached = selectionCache.get(context.taskId());
        List<String> selectedTables;
        if (cached != null && cached.configId().equals(databaseConfigId) && cached.catalogNames().equals(catalogNames)) {
            selectedTables = cached.selectedTables();
            LOGGER.info("EMS 辅助查询：复用选表缓存 {}", selectedTables);
        } else {
            progress.accept("正在匹配相关数据表");
            int maxTables = snapshot.isPresent() ? 10 : 5;
            selectedTables = modelClient.selectRelevantTables(context, catalog, maxTables).stream()
                    .filter(name -> catalogNames.contains(name))
                    .distinct().limit(maxTables).toList();
            selectionCache.put(context.taskId(), new CachedSelection(databaseConfigId, catalogNames, selectedTables));
            LOGGER.info("EMS 辅助查询：模型选表 {}", selectedTables);
        }
        if (!selectedTables.isEmpty()) {
            progress.accept("正在读取数据表字段");
        }
        Map<String, List<EmsDatabaseColumnMetadata>> tableColumns = new LinkedHashMap<>();
        for (String table : selectedTables) {
            String[] parts = table.split("\\.");
            if (parts.length != 2) {
                continue;
            }
            try {
                if (snapshot.isPresent()) {
                    matches.stream().filter(item -> item.qualifiedName().equals(table)).findFirst()
                            .ifPresent(item -> tableColumns.put(table, snapshot.orElseThrow().relevantColumns(item, searchText)));
                } else {
                    tableColumns.put(table, executor.listColumns(config, parts[0], parts[1]));
                }
            } catch (RuntimeException ignored) {
                LOGGER.info("EMS 辅助查询：读取 {}.{} 字段失败，已跳过", parts[0], parts[1]);
            }
        }
        List<EmsDatabaseEvidence> evidence = new ArrayList<>();
        if (context.databaseEvidence() != null) {
            evidence.addAll(context.databaseEvidence());
        }
        progress.accept("正在规划只读查询");
        EmsDatabaseQueryPlan plan = modelClient.planEmsDatabaseQuery(context, catalog, evidence, tableColumns);
        LOGGER.info("EMS 辅助查询：计划步骤 {} 个", plan.steps().size());
        Map<String, EmsDatabaseQueryResult> results = new LinkedHashMap<>();
        Set<String> completed = new java.util.HashSet<>();
        Set<String> noDataTables = new java.util.HashSet<>();
        int stepIndex = 0;
        for (EmsDatabaseQueryPlan.Step step : orderedSteps(plan.steps())) {
            stepIndex++;
            if (!completed.containsAll(step.dependsOn())) {
                LOGGER.info("EMS 辅助查询：跳过依赖未完成的步骤 {}", step.id());
                continue;
            }
            Map<String, Object> parameters = new LinkedHashMap<>();
            boolean bindingMissing = false;
            for (Map.Entry<String, EmsDatabaseQueryPlan.Binding> entry : step.bindings().entrySet()) {
                EmsDatabaseQueryPlan.Binding binding = entry.getValue();
                if (binding.value() != null) {
                    parameters.put(entry.getKey(), binding.value());
                    continue;
                }
                EmsDatabaseQueryResult sourceResult = results.get(binding.stepId());
                int columnIndex = sourceResult == null ? -1 : sourceResult.columns().indexOf(binding.column());
                if (columnIndex < 0 || sourceResult.rows().isEmpty()) {
                    bindingMissing = true;
                    break;
                }
                parameters.put(entry.getKey(), sourceResult.rows().get(0).get(columnIndex));
            }
            if (bindingMissing) {
                LOGGER.info("EMS 辅助查询：步骤 {} 绑定值缺失，已跳过", step.id());
                continue;
            }
            progress.accept("正在执行第 " + stepIndex + " 步查询");
            try {
                String sql = sqlGuard.validate(step.sql());
                String[] source = sourceTable(sql);
                String qualified = source[0] + "." + source[1];
                if (noDataTables.contains(qualified)) {
                    LOGGER.info("EMS 辅助查询：{} 已确认无数据，跳过重复查询", qualified);
                    continue;
                }
                EmsDatabaseQueryResult result = executor.execute(config, sql, parameters);
                results.put(step.id(), result);
                if (result.rows().isEmpty()) {
                    noDataTables.add(qualified);
                    evidence.add(new EmsDatabaseEvidence(source[0], source[1],
                            "该表已确认无数据，请勿重复查询", List.of(), List.of()));
                    LOGGER.info("EMS 辅助查询：步骤 {} {}.{} 返回 0 行", step.id(), source[0], source[1]);
                    continue;
                }
                evidence.add(new EmsDatabaseEvidence(source[0], source[1], step.purpose(), result.columns(),
                        result.rows()));
                completed.add(step.id());
                LOGGER.info("EMS 辅助查询：步骤 {} {}.{} 返回 {} 行", step.id(), source[0], source[1],
                        result.rows().size());
            } catch (RuntimeException exception) {
                String[] source = sourceTable(step.sql());
                evidence.add(new EmsDatabaseEvidence(source[0], source[1], "该步骤查询失败，已跳过", List.of(), List.of()));
                LOGGER.warn("EMS 辅助查询步骤 {} 失败：{}", step.id(), exception.getClass().getSimpleName());
            }
        }
        LOGGER.info("EMS 辅助查询累计证据 {} 条", evidence.size());
        return List.copyOf(evidence);
        } catch (EmsSchemaCatalogNoMatchException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            LOGGER.warn("EMS 辅助查询降级：{} - {}", exception.getClass().getSimpleName(), exception.getMessage());
            return List.of();
        }
    }

    /** 配置类数据只在 basedata；其他库仅保留人员定位所需表。 */
    static List<EmsDatabaseTableMetadata> coarseFilter(List<EmsDatabaseTableMetadata> catalog) {
        return catalog.stream()
                .filter(item -> {
                    String lower = (item.schema() + "." + item.table() + " " + item.comment()).toLowerCase(Locale.ROOT);
                    if ("basedata".equals(item.schema())) {
                        return FILTER_KEYWORDS.stream().anyMatch(lower::contains);
                    }
                    return lower.contains("employee") || lower.contains("staff") || lower.contains("person");
                })
                .toList();
    }

    /** 从 SQL 的 FROM 子句提取首个 schema.table 作为证据溯源。 */
    private static List<EmsDatabaseQueryPlan.Step> orderedSteps(List<EmsDatabaseQueryPlan.Step> steps) {
        List<EmsDatabaseQueryPlan.Step> remaining = new ArrayList<>(steps);
        List<EmsDatabaseQueryPlan.Step> ordered = new ArrayList<>();
        Set<String> done = new java.util.HashSet<>();
        while (!remaining.isEmpty()) {
            boolean progressed = false;
            for (java.util.Iterator<EmsDatabaseQueryPlan.Step> iterator = remaining.iterator(); iterator.hasNext();) {
                EmsDatabaseQueryPlan.Step step = iterator.next();
                if (done.containsAll(step.dependsOn())) {
                    ordered.add(step);
                    done.add(step.id());
                    iterator.remove();
                    progressed = true;
                }
            }
            if (!progressed) {
                break;
            }
        }
        return ordered;
    }

    public void invalidateSelection(UUID taskId) {
        selectionCache.remove(taskId);
    }

    private record CachedSelection(UUID configId, List<String> catalogNames, List<String> selectedTables) {
    }

    private static String[] sourceTable(String sql) {
        Matcher matcher = FROM_SOURCE.matcher(sql);
        if (matcher.find()) {
            return new String[] { matcher.group(1), matcher.group(2) };
        }
        return new String[] { "unknown", "unknown" };
    }
}
