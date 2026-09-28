package cn.com.crc.ems.finance.agent.tool.application;

import java.util.Set;

/** 三类字段权限策略；留存和送模字段必须从可读字段中选择。 */
public record FieldAccessPolicy(Set<String> readFields, Set<String> retainFields, Set<String> modelFields) {

    public FieldAccessPolicy {
        if (readFields == null || retainFields == null || modelFields == null || readFields.isEmpty()
                || !readFields.containsAll(retainFields) || !readFields.containsAll(modelFields)) {
            throw new IllegalArgumentException("retain/model fields must be readable");
        }
        readFields = normalized(readFields);
        retainFields = normalized(retainFields);
        modelFields = normalized(modelFields);
    }

    private static Set<String> normalized(Set<String> fields) {
        if (fields.stream().anyMatch(field -> field == null || field.isBlank() || field.contains("."))) {
            throw new IllegalArgumentException("field names must be non-blank top-level names");
        }
        return Set.copyOf(fields);
    }
}
