package cn.com.crc.ems.finance.agent.shared.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 *
 * @Description: 统一执行严格 JSON 解析和确定性 canonical JSON 编码。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public final class CanonicalJsonCodec {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .nodeFactory(JsonNodeFactory.withExactBigDecimals(true))
            .build();

    private CanonicalJsonCodec() {
    }

    /** 严格解析 JSON，拒绝尾随 token 和非法 JSON。 */
    public static JsonNode parse(String json) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("json must not be blank");
        }
        try {
            return JSON_MAPPER.readTree(json.trim());
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("json must be valid JSON", exception);
        }
    }

    /** 对任意 JSON 节点按固定规则编码为 canonical JSON。 */
    public static String canonicalize(JsonNode node) {
        if (node == null) {
            throw new IllegalArgumentException("json node is required");
        }
        StringBuilder output = new StringBuilder();
        write(node, output);
        return output.toString();
    }

    private static void write(JsonNode node, StringBuilder output) {
        if (node.isObject()) {
            List<String> names = new ArrayList<>();
            node.fieldNames().forEachRemaining(names::add);
            Collections.sort(names);
            output.append('{');
            for (int index = 0; index < names.size(); index++) {
                if (index > 0) {
                    output.append(',');
                }
                writeString(names.get(index), output);
                output.append(':');
                write(node.get(names.get(index)), output);
            }
            output.append('}');
            return;
        }
        if (node.isArray()) {
            output.append('[');
            Iterator<JsonNode> values = node.elements();
            boolean first = true;
            while (values.hasNext()) {
                if (!first) {
                    output.append(',');
                }
                write(values.next(), output);
                first = false;
            }
            output.append(']');
            return;
        }
        if (node.isNumber()) {
            BigDecimal value;
            try {
                value = new BigDecimal(node.asText());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("JSON number is not finite or supported", exception);
            }
            if (value.signum() == 0) {
                output.append('0');
            } else {
                output.append(value.stripTrailingZeros().toPlainString());
            }
            return;
        }
        if (node.isTextual()) {
            writeString(node.textValue(), output);
            return;
        }
        output.append(node.toString());
    }

    private static void writeString(String value, StringBuilder output) {
        try {
            output.append(JSON_MAPPER.writeValueAsString(value));
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("JSON string cannot be encoded", exception);
        }
    }
}
