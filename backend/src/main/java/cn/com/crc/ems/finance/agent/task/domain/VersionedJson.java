package cn.com.crc.ems.finance.agent.task.domain;

import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;

import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJson;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;

/**
 *
 * @Description: 带 schema 版本的 JSON 文档值对象，使用 Jackson 严格校验 JSON 容器。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 23:45:00
 */
public record VersionedJson(String schemaVersion, String json) {

    public VersionedJson {
        if (schemaVersion == null || schemaVersion.isBlank()) {
            throw new IllegalArgumentException("schemaVersion must not be blank");
        }
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("json must not be blank");
        }
        String value = json.trim();
        JsonNode parsed = parse(value);
        if (!parsed.isObject() && !parsed.isArray()) {
            throw new IllegalArgumentException("json must be an object or array");
        }
    }

    /** 要求当前文档为 JSON object，匹配数据库对象选择约束。 */
    public void requireObject() {
        JsonNode parsed = parse(Objects.requireNonNull(json).trim());
        if (!parsed.isObject()) {
            throw new IllegalArgumentException("json must be an object");
        }
    }

    /** 返回使用共享规则编码的 canonical JSON 值。 */
    public CanonicalJson canonical() {
        return CanonicalJson.of(json);
    }

    /**
     *
     * @description: 使用 Jackson 严格解析 JSON，拒绝语法错误和尾随 token。
     *
     * @param: value 待解析的 JSON 文本。
     * @return: Jackson 解析后的 JSON 节点。
     * @throws: IllegalArgumentException JSON 语法非法或无法解析时抛出。
     * @author: yigeyige99
     */
    private static JsonNode parse(String value) {
        return CanonicalJsonCodec.parse(value);
    }
}
