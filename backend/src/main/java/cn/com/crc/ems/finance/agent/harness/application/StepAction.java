package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;
import java.util.UUID;

/** 步骤提议中的单个只读工具动作。 */
public record StepAction(String goal, String toolCode, UUID sourceBindingId, UUID scopeRevisionId,
        List<String> requestedLogicalFields, String queryTemplateVersion, int maxCalls) {

    public StepAction {
        requireText(goal, "goal");
        requireText(toolCode, "toolCode");
        if (sourceBindingId == null || scopeRevisionId == null || requestedLogicalFields == null
                || requestedLogicalFields.isEmpty() || requestedLogicalFields.stream().anyMatch(value -> value == null || value.isBlank())
                || requestedLogicalFields.size() != requestedLogicalFields.stream().distinct().count()) {
            throw new IllegalArgumentException("step action fields are invalid");
        }
        requestedLogicalFields = List.copyOf(requestedLogicalFields);
        requireText(queryTemplateVersion, "queryTemplateVersion");
        if (maxCalls < 1 || maxCalls > 100) {
            throw new IllegalArgumentException("maxCalls must be between 1 and 100");
        }
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank() || value.length() > 2000) {
            throw new IllegalArgumentException(field + " is invalid");
        }
    }
}
