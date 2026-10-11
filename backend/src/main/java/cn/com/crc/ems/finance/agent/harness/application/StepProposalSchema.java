package cn.com.crc.ems.finance.agent.harness.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @Description: 按方案附件 B 校验下一步提议，拒绝额外字段和非法结构。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public final class StepProposalSchema implements JsonSchema<StepProposal> {

    private static final Set<String> ROOT_FIELDS = Set.of("recommended", "alternatives", "reason", "stopAssessment");
    private static final Set<String> ACTION_FIELDS = Set.of("goal", "toolCode", "sourceBindingId", "scopeRevisionId",
            "requestedLogicalFields", "queryTemplateVersion", "maxCalls");

    @Override
    public String schemaRef() {
        return "step-proposal-v1";
    }

    @Override
    public StepProposal validate(JsonNode value) {
        JsonNode root = ModelGateway.requireObject(value, "step proposal");
        requireFields(root, ROOT_FIELDS, "step proposal");
        StepAction recommended = action(root.get("recommended"));
        JsonNode alternativesNode = root.get("alternatives");
        if (alternativesNode == null || !alternativesNode.isArray() || alternativesNode.size() > 2) {
            throw invalid();
        }
        List<StepAction> alternatives = new ArrayList<>();
        alternativesNode.forEach(item -> alternatives.add(action(item)));
        JsonNode reason = root.get("reason");
        JsonNode stop = root.get("stopAssessment");
        if (reason == null || !reason.isTextual() || reason.textValue().length() > 2000
                || stop == null || !stop.isTextual()) {
            throw invalid();
        }
        StopAssessment assessment;
        try {
            assessment = StopAssessment.valueOf(stop.textValue());
        } catch (IllegalArgumentException exception) {
            throw invalid();
        }
        return new StepProposal(recommended, alternatives, reason.textValue(), assessment);
    }

    private static StepAction action(JsonNode value) {
        JsonNode node = ModelGateway.requireObject(value, "step action");
        requireFields(node, ACTION_FIELDS, "step action");
        try {
            String goal = text(node, "goal");
            String toolCode = text(node, "toolCode");
            UUID sourceBindingId = UUID.fromString(text(node, "sourceBindingId"));
            UUID scopeRevisionId = UUID.fromString(text(node, "scopeRevisionId"));
            JsonNode fields = node.get("requestedLogicalFields");
            if (!fields.isArray() || fields.isEmpty()) {
                throw invalid();
            }
            List<String> requested = new ArrayList<>();
            fields.forEach(item -> {
                if (!item.isTextual() || item.textValue().isBlank()) {
                    throw invalid();
                }
                requested.add(item.textValue());
            });
            if (requested.size() != requested.stream().distinct().count()) {
                throw invalid();
            }
            String template = text(node, "queryTemplateVersion");
            JsonNode maxCalls = node.get("maxCalls");
            if (!maxCalls.isIntegralNumber() || !maxCalls.canConvertToInt()) {
                throw invalid();
            }
            return new StepAction(goal, toolCode, sourceBindingId, scopeRevisionId, requested, template,
                    maxCalls.intValue());
        } catch (RuntimeException exception) {
            if (exception instanceof ModelGatewayException modelException) {
                throw modelException;
            }
            throw invalid();
        }
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || !value.isTextual() || value.textValue().isBlank()) {
            throw invalid();
        }
        return value.textValue();
    }

    private static void requireFields(JsonNode node, Set<String> fields, String name) {
        Iterator<String> names = node.fieldNames();
        while (names.hasNext()) {
            if (!fields.contains(names.next())) {
                throw new ModelGatewayException("SCHEMA_INVALID", name + " contains an extra field");
            }
        }
        if (!node.fieldNames().hasNext()) {
            throw invalid();
        }
        for (String field : fields) {
            if (!node.has(field)) {
                throw invalid();
            }
        }
    }

    private static ModelGatewayException invalid() {
        return new ModelGatewayException("SCHEMA_INVALID", "model output failed schema validation");
    }
}
