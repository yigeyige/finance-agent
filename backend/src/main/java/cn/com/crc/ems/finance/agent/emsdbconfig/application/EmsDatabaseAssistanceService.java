package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseEvidence;
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseQueryRequest;

/**
 *
 * @Description: 将模型的可选查询建议置于连接配置与只读 SQL 守卫之后执行，支持表目录粗筛与多轮累积证据。
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
            "category", "claim");
    private static final Pattern FROM_SOURCE = Pattern.compile("(?i)\\bFROM\\s+`?([a-z0-9_]+)`?\\.`?([a-z0-9_]+)`?");

    private final EmsDatabaseConnectionConfigService configService;
    private final EmsReadOnlySqlGuard sqlGuard;
    private final EmsDatabaseQueryExecutor executor;

    public EmsDatabaseAssistanceService(EmsDatabaseConnectionConfigService configService, EmsReadOnlySqlGuard sqlGuard,
            EmsDatabaseQueryExecutor executor) {
        this.configService = configService;
        this.sqlGuard = sqlGuard;
        this.executor = executor;
    }

    /** 无启用连接、不需查询或任一受控失败时静默降级，不影响原有模型指导。 */
    public List<EmsDatabaseEvidence> collect(BusinessModelClient modelClient, BusinessGuidanceContext context) {
        try {
            EmsDatabaseConnectionConfig config = configService.requireEnabled();
        List<EmsDatabaseTableMetadata> catalog = coarseFilter(executor.listAllowedTables(config));
        LOGGER.info("EMS 辅助查询：候选表 {} 张", catalog.size());
        if (catalog.isEmpty()) {
            return List.of();
        }
        List<String> selectedTables = modelClient.selectRelevantTables(context, catalog);
        LOGGER.info("EMS 辅助查询：模型选表 {}", selectedTables);
        Map<String, List<EmsDatabaseColumnMetadata>> tableColumns = new LinkedHashMap<>();
        for (String table : selectedTables) {
            String[] parts = table.split("\\.");
            if (parts.length != 2) {
                continue;
            }
            try {
                tableColumns.put(table, executor.listColumns(config, parts[0], parts[1]));
            } catch (RuntimeException ignored) {
                LOGGER.info("EMS 辅助查询：读取 {}.{} 字段失败，已跳过", parts[0], parts[1]);
            }
        }
        List<EmsDatabaseEvidence> evidence = new ArrayList<>();
        for (int round = 0; round < MAX_ROUNDS; round++) {
            Optional<EmsDatabaseQueryRequest> request = modelClient.planEmsDatabaseQuery(context, catalog, evidence,
                    tableColumns);
            LOGGER.info("EMS 辅助查询第 {} 轮：needQuery={}", round + 1, request.isPresent());
            if (request.isEmpty()) {
                break;
            }
            String sql = sqlGuard.validate(request.get().sql());
            EmsDatabaseQueryResult result = executor.execute(config, sql);
            String[] source = sourceTable(sql);
            evidence.add(new EmsDatabaseEvidence(source[0], source[1], request.get().purpose(), result.columns(),
                    result.rows()));
            LOGGER.info("EMS 辅助查询第 {} 轮完成：{}.{} 返回 {} 行", round + 1, source[0], source[1],
                    result.rows().size());
        }
        LOGGER.info("EMS 辅助查询累计证据 {} 条", evidence.size());
        return List.copyOf(evidence);
        } catch (RuntimeException exception) {
            LOGGER.warn("EMS 辅助查询降级：{} - {}", exception.getClass().getSimpleName(), exception.getMessage());
            return List.of();
        }
    }

    /** 配置类数据只在 basedata；其他库仅保留人员定位所需表。 */
    private static List<EmsDatabaseTableMetadata> coarseFilter(List<EmsDatabaseTableMetadata> catalog) {
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
    private static String[] sourceTable(String sql) {
        Matcher matcher = FROM_SOURCE.matcher(sql);
        if (matcher.find()) {
            return new String[] { matcher.group(1), matcher.group(2) };
        }
        return new String[] { "unknown", "unknown" };
    }
}
