package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

/**
 *
 * @Description: 在 JDBC 语句创建前强制 EMS Agent 查询为单条受控只读 SQL。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Component
public final class EmsReadOnlySqlGuard {
    private static final Set<String> ALLOWED_SCHEMAS = Set.of("basedata", "activiti_ems", "account", "budget",
            "payment", "quality", "smart_audit", "mobile");
    private static final Pattern FORBIDDEN = Pattern.compile("(?i)\\b(INSERT|UPDATE|DELETE|TRUNCATE|DROP|ALTER|CREATE|REPLACE|MERGE|GRANT|REVOKE|CALL|EXECUTE|PREPARE|DEALLOCATE|SET|USE|LOCK|UNLOCK|HANDLER|KILL|DO|LOAD|OUTFILE|DUMPFILE|INTO|SHOW|EXPLAIN|DESCRIBE|WITH|UNION|SLEEP|BENCHMARK|LOAD_FILE)\\b");
    private static final Pattern FROM_OR_JOIN = Pattern.compile("(?i)\\b(?:FROM|JOIN)\\s+`?([a-z0-9_]+)`?\\.`?([a-z0-9_]+)`?");
    private static final Pattern FROM_OR_JOIN_TOKEN = Pattern.compile("(?i)\\b(?:FROM|JOIN)\\b");
    private static final Pattern UNSAFE_COMMENT = Pattern.compile("(?s)(--|#|/\\*|\\*/)" );
    private static final Pattern COMMA_JOIN = Pattern.compile("(?i)\\b(?:FROM|JOIN)\\s+[^\\s]+\\s*,");

    /**
     * @description: 校验并返回规范化 SQL；任何不确定语法均以拒绝处理。
     * @param sql Agent 提议的 SQL
     * @return 可交给 JDBC executeQuery 的单条 SELECT
     */
    public String validate(String sql) {
        if (sql == null || sql.isBlank() || sql.length() > 4_000) {
            throw rejected();
        }
        String value = sql.trim();
        String lower = value.toLowerCase(Locale.ROOT);
        if (!lower.startsWith("select ") || value.indexOf(';') >= 0 || UNSAFE_COMMENT.matcher(value).find()
                || FORBIDDEN.matcher(value).find() || COMMA_JOIN.matcher(value).find()) {
            throw rejected();
        }
        Matcher source = FROM_OR_JOIN.matcher(value);
        int sourceCount = 0;
        while (source.find()) {
            sourceCount++;
            if (!ALLOWED_SCHEMAS.contains(source.group(1).toLowerCase(Locale.ROOT))) {
                throw rejected();
            }
        }
        if (sourceCount == 0 || sourceCount != count(FROM_OR_JOIN_TOKEN.matcher(value))) {
            throw rejected();
        }
        return value;
    }

    private static int count(Matcher matcher) {
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private static IllegalArgumentException rejected() {
        return new IllegalArgumentException("仅允许受控 EMS 业务库中的单条 SELECT 查询");
    }
}
