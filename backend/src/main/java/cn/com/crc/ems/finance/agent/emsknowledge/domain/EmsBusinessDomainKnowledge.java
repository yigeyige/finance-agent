package cn.com.crc.ems.finance.agent.emsknowledge.domain;

import java.util.List;
import java.util.Objects;

/**
 *
 * @Description: EMS 业务域的受控结构知识，不承载实时业务数据。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 00:00:00
 */
public record EmsBusinessDomainKnowledge(String code, String name, String summary, List<String> schemas,
        int tableCount, List<String> keyTables, String knowledgeBoundary, String verificationHint) {

    /** 校验目录项的稳定展示字段，阻止不完整知识进入接口。 */
    public EmsBusinessDomainKnowledge {
        requireText(code, "code");
        requireText(name, "name");
        requireText(summary, "summary");
        requireText(knowledgeBoundary, "knowledgeBoundary");
        requireText(verificationHint, "verificationHint");
        if (tableCount < 1) {
            throw new IllegalArgumentException("tableCount must be positive");
        }
        schemas = immutableTexts(schemas, "schemas");
        keyTables = immutableTexts(keyTables, "keyTables");
    }

    private static List<String> immutableTexts(List<String> values, String fieldName) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be empty");
        }
        values.forEach(value -> requireText(value, fieldName));
        return List.copyOf(values);
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        Objects.requireNonNull(value, fieldName + " is required");
    }
}
