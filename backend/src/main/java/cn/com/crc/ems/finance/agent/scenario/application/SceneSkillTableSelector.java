package cn.com.crc.ems.finance.agent.scenario.application;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

/**
 * @Description: 场景预填前的表名筛选：按文本 schema 初筛，并排除日志、临时、备份、MQ 与事务类表。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 17:10:00
 */
@Component
public class SceneSkillTableSelector {

    private static final Pattern EXCLUDED_SEGMENT = Pattern.compile(
            "(^|[._])(log|logs|tmp|temp|temporary|bak|backup|his|history|mq|msg|message|messages|queue|queues|topic|tx|trans|transaction|transactions)([._]|$)");
    private static final Pattern DATE_SUFFIX = Pattern.compile(
            "[._](\\d{6}|\\d{8}|20\\d{6}|20\\d{2}_\\d{2}_\\d{2})$");

    /**
     * @description: 计算本次可交给模型的表名；筛选后为空也返回空列表，由调用方决定提示。
     * @param: qualifiedTables 实例允许的 `schema.table` 列表
     * @param: text 用户输入描述，用于识别 schema
     * @return: 选中的表、排除数量与命中的 schema
     * @author: yigeyige99
     */
    public TableSelection select(List<String> qualifiedTables, String text) {
        List<String> distinct = qualifiedTables == null ? List.of()
                : qualifiedTables.stream().distinct().sorted().toList();
        String loweredText = text == null ? "" : text.toLowerCase(Locale.ROOT);
        List<String> schemas = distinct.stream().map(SceneSkillTableSelector::schemaOf).distinct().toList();
        List<String> mentioned = schemas.stream().filter(schema -> containsWord(loweredText, schema)).toList();
        List<String> scoped = mentioned.isEmpty() ? distinct
                : distinct.stream().filter(table -> mentioned.contains(schemaOf(table))).toList();
        List<String> selected = scoped.stream().filter(table -> !excluded(table)).toList();
        return new TableSelection(selected, distinct.size() - selected.size(), mentioned);
    }

    private static boolean excluded(String qualifiedTable) {
        String table = tableOf(qualifiedTable).toLowerCase(Locale.ROOT);
        return EXCLUDED_SEGMENT.matcher(table).find() || DATE_SUFFIX.matcher(table).find();
    }

    private static boolean containsWord(String text, String word) {
        return Pattern.compile("(?<![a-z0-9_])" + Pattern.quote(word) + "(?![a-z0-9_])").matcher(text).find();
    }

    private static String schemaOf(String qualifiedTable) {
        int dot = qualifiedTable.indexOf('.');
        return dot < 0 ? qualifiedTable : qualifiedTable.substring(0, dot);
    }

    private static String tableOf(String qualifiedTable) {
        int dot = qualifiedTable.indexOf('.');
        return dot < 0 ? qualifiedTable : qualifiedTable.substring(dot + 1);
    }

    /** 表名筛选结果；excludedCount 同时包含 schema 初筛和规则排除。 */
    public record TableSelection(List<String> tables, int excludedCount, List<String> schemas) {
        public TableSelection {
            tables = tables == null ? List.of() : List.copyOf(tables);
            schemas = schemas == null ? List.of() : List.copyOf(schemas);
            if (excludedCount < 0) throw new IllegalArgumentException("excluded table count is invalid");
        }
    }
}
