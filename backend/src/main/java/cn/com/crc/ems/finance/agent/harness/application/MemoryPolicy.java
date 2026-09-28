package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @Description: 长期记忆文本的非敏感经验和 Prompt 注入策略。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 11:00:00
 */
public final class MemoryPolicy {

    private static final List<Pattern> FORBIDDEN = List.of(
            Pattern.compile("(?i)\\b(password|secret|token|credential|access[_ -]?key|api[_ -]?key)\\b"),
            Pattern.compile("(?i)(personnel|employee|staff|user)\\s*(id|number|no|code)"),
            Pattern.compile("配置|凭证|人员编号|员工编号|身份证|业务决定|结论|批准|发布"),
            Pattern.compile("(?i)(ignore|disregard|override).{0,60}(previous|system|policy|instruction)"),
            Pattern.compile("(?i)system\\s+prompt|prompt\\s+injection"));

    private MemoryPolicy() {
    }

    /** 校验只能进入人工审批队列的非敏感经验摘要。 */
    public static void requireSafeSummary(String summary) {
        if (summary == null || summary.isBlank() || summary.length() > 500) {
            throw new IllegalArgumentException("memory summary must be non-blank and at most 500 characters");
        }
        FORBIDDEN.stream().filter(pattern -> pattern.matcher(summary).find()).findFirst()
                .ifPresent(pattern -> {
                    throw new IllegalArgumentException("memory summary contains forbidden content");
                });
    }
}
