package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 实例配置结构目录的名称过滤和本地关键词选表。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 20:00:00
 */
public record EmsSchemaCatalog(List<EmsSchemaTable> tables) {
    private static final Set<String> SCHEMAS = Set.of("basedata", "activiti_ems");
    private static final Pattern EXCLUDED_PART = Pattern.compile("(^|_)(log|logs|hist|history|bak|backup|tmp|temp|test|copy|old)(_|[0-9]|$)");
    private static final Pattern DATE_SUFFIX = Pattern.compile("(?:_|[a-z])(?:[0-9]{8,9}|[0-9]{4})$");
    private static final Pattern WORDS = Pattern.compile("[a-z][a-z0-9_]{1,}|[\u4e00-\u9fff]{2,}");

    public EmsSchemaCatalog { tables = List.copyOf(tables); }

    /** 排除日志、历史、备份和技术流水，只接收约定业务库。 */
    public static boolean includeName(String schema, String table) {
        if (!SCHEMAS.contains(schema) || table == null) return false;
        String name = table.toLowerCase(Locale.ROOT);
        return !EXCLUDED_PART.matcher(name).find() && !DATE_SUFFIX.matcher(name).find()
                && !name.startsWith("act_hi_") && !name.startsWith("act_ru_")
                && !name.startsWith("act_evt_") && !name.startsWith("t_dsb_tx_")
                && !name.startsWith("imp_") && !name.contains("_mq_") && !name.startsWith("mq_");
    }

    /** 将用户业务词与表及字段注释匹配，按命中强度限制候选规模。 */
    public List<EmsSchemaTable> search(String query, int limit) {
        if (query == null || query.isBlank() || limit < 1) return List.of();
        List<String> terms = terms(query);
        return tables.stream().map(table -> new Scored(table, score(table, terms)))
                .filter(item -> item.score() > 0)
                .sorted(Comparator.comparingInt(Scored::score).reversed()
                        .thenComparing(item -> item.table().qualifiedName()))
                .limit(limit).map(Scored::table).toList();
    }

    /** 把与业务词匹配的字段排在前面，确保模型的字段预算能看到命中字段。 */
    public List<EmsDatabaseColumnMetadata> relevantColumns(EmsSchemaTable table, String query) {
        List<String> terms = terms(query);
        return table.columns().stream()
                .sorted(Comparator.comparingInt((EmsDatabaseColumnMetadata column) ->
                        fieldScore(column, terms)).reversed())
                .limit(20).toList();
    }

    private static List<String> terms(String query) {
        String normalized = query.toLowerCase(Locale.ROOT)
                .replaceAll("查询|查看|分析|处理|请|需要|业务|配置|当前|相关", " ");
        List<String> terms = new ArrayList<>();
        Matcher matcher = WORDS.matcher(normalized);
        while (matcher.find()) {
            String word = matcher.group();
            terms.add(word);
            if (word.codePoints().allMatch(c -> c >= 0x4e00 && c <= 0x9fff) && word.length() > 2) {
                for (int i = 0; i < word.length() - 1; i++) terms.add(word.substring(i, i + 2));
            }
        }
        return terms;
    }

    private static int score(EmsSchemaTable table, List<String> terms) {
        String name = (table.schema() + "." + table.table()).toLowerCase(Locale.ROOT);
        String comment = table.comment().toLowerCase(Locale.ROOT);
        String fields = table.columns().stream()
                .map(column -> column.name() + " " + column.comment())
                .reduce("", (left, right) -> left + " " + right).toLowerCase(Locale.ROOT);
        int score = 0;
        for (String term : terms) {
            if (name.contains(term)) score += 4;
            if (comment.contains(term)) score += 3;
            if (fields.contains(term)) score += 1;
        }
        return score;
    }

    private static int fieldScore(EmsDatabaseColumnMetadata column, List<String> terms) {
        String text = (column.name() + " " + column.comment()).toLowerCase(Locale.ROOT);
        return (int) terms.stream().filter(text::contains).count();
    }

    private record Scored(EmsSchemaTable table, int score) { }
}
