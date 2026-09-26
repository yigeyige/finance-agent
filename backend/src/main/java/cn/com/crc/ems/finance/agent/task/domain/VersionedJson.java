package cn.com.crc.ems.finance.agent.task.domain;

import java.util.Objects;

/** 带 schema 版本的 JSON 文档值对象，避免未声明结构进入范围模型。 */
public record VersionedJson(String schemaVersion, String json) {

    public VersionedJson {
        if (schemaVersion == null || schemaVersion.isBlank()) {
            throw new IllegalArgumentException("schemaVersion must not be blank");
        }
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("json must not be blank");
        }
        String value = json.trim();
        if (!((value.startsWith("{") && value.endsWith("}"))
                || (value.startsWith("[") && value.endsWith("]")))) {
            throw new IllegalArgumentException("json must be an object or array");
        }
    }

    /** 要求当前文档为 JSON object，匹配数据库对象选择约束。 */
    public void requireObject() {
        String value = Objects.requireNonNull(json).trim();
        if (!value.startsWith("{") || !value.endsWith("}")) {
            throw new IllegalArgumentException("json must be an object");
        }
    }
}
