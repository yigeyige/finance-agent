package cn.com.crc.ems.finance.agent.tool.application;

import com.fasterxml.jackson.databind.JsonNode;

/** 源响应经过完整可读校验后的三类内存投影。 */
public record TrustedProjection(JsonNode readPayload, JsonNode retainedPayload, JsonNode modelPayload) {

    public TrustedProjection {
        if (readPayload == null || retainedPayload == null || modelPayload == null) {
            throw new IllegalArgumentException("projection payloads are required");
        }
        readPayload = readPayload.deepCopy();
        retainedPayload = retainedPayload.deepCopy();
        modelPayload = modelPayload.deepCopy();
    }
}
