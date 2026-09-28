package cn.com.crc.ems.finance.agent.packet.application;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

/** 校验决策包结构和发布前必要依据，避免不完整草稿进入复核。 */
@Component
public final class PacketDocumentValidator {

    /** 校验结构化决策包的必备章节及业务影响显式确认。 */
    public void validate(JsonNode document) {
        if (document == null || !document.isObject()) {
            throw new IllegalArgumentException("packet document must be an object");
        }
        requireText(document, "problem");
        requireNode(document, "premises");
        requireNode(document, "scope");
        requireArray(document, "sources");
        JsonNode facts = requireObject(document, "facts");
        requireArray(facts, "current");
        requireArray(facts, "future");
        requireArray(facts, "undetermined");
        requireNode(document, "computations");
        requireArray(document, "professionalJudgments");
        requireArray(document, "recommendations");
        JsonNode business = requireObject(document, "businessConfirmations");
        if (!business.has("impactConfirmed") || !business.get("impactConfirmed").isBoolean()) {
            throw new IllegalArgumentException("businessConfirmations.impactConfirmed must be explicit");
        }
        requireArray(business, "pendingItems");
        requireObject(document, "validationPreparation");
        requireObject(document, "handoff");
    }

    private static JsonNode requireNode(JsonNode parent, String name) {
        JsonNode value = parent.get(name);
        if (value == null || value.isNull()) {
            throw new IllegalArgumentException("packet field is required: " + name);
        }
        return value;
    }

    private static JsonNode requireObject(JsonNode parent, String name) {
        JsonNode value = requireNode(parent, name);
        if (!value.isObject()) {
            throw new IllegalArgumentException("packet field must be an object: " + name);
        }
        return value;
    }

    private static void requireArray(JsonNode parent, String name) {
        if (!requireNode(parent, name).isArray()) {
            throw new IllegalArgumentException("packet field must be an array: " + name);
        }
    }

    private static void requireText(JsonNode parent, String name) {
        JsonNode value = requireNode(parent, name);
        if (!value.isTextual() || value.asText().isBlank()) {
            throw new IllegalArgumentException("packet field must be non-empty text: " + name);
        }
    }
}
