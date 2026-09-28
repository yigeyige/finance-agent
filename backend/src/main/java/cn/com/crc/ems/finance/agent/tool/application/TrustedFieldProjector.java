package cn.com.crc.ems.finance.agent.tool.application;

import java.util.Iterator;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/** 在易失内存中完成全量可读校验和 retain/model 可信投影。 */
@Component
public class TrustedFieldProjector {

    public TrustedProjection project(JsonNode source, FieldAccessPolicy policy) {
        if (source == null || !source.isObject() || policy == null) {
            throw new IllegalArgumentException("source object and field policy are required");
        }
        SensitiveFieldScanner.requireSafe(source);
        ObjectNode read = (ObjectNode) source.deepCopy();
        Iterator<String> fields = read.fieldNames();
        while (fields.hasNext()) {
            if (!policy.readFields().contains(fields.next())) {
                throw new IllegalArgumentException("source returned a field outside read policy");
            }
        }
        ObjectNode retained = select(read, policy.retainFields());
        ObjectNode model = select(read, policy.modelFields());
        SensitiveFieldScanner.requireSafe(retained);
        SensitiveFieldScanner.requireSafe(model);
        return new TrustedProjection(read, retained, model);
    }

    private static ObjectNode select(ObjectNode source, java.util.Set<String> fields) {
        ObjectNode result = JsonNodeFactory.instance.objectNode();
        fields.forEach(field -> {
            JsonNode value = source.get(field);
            if (value != null) {
                result.set(field, value.deepCopy());
            }
        });
        return result;
    }
}
