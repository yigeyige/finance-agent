package cn.com.crc.ems.finance.agent.harness.application;

import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;

/** Harness 唯一的结构化模型调用入口。 */
public interface ModelGateway {

    <T> ModelResult<T> generateStructured(ModelPurpose purpose, PromptTemplateVersion template,
            AllowedContext context, JsonSchema<T> outputSchema, ModelBudget budget,
            CancellationToken cancellationToken);

    static void requireRequest(ModelPurpose purpose, PromptTemplateVersion template,
            AllowedContext context, JsonSchema<?> outputSchema, ModelBudget budget,
            CancellationToken cancellationToken) {
        Objects.requireNonNull(purpose, "purpose is required");
        Objects.requireNonNull(template, "template is required");
        Objects.requireNonNull(context, "context is required");
        Objects.requireNonNull(outputSchema, "outputSchema is required");
        Objects.requireNonNull(budget, "budget is required");
        Objects.requireNonNull(cancellationToken, "cancellationToken is required");
    }

    static JsonNode requireObject(JsonNode value, String field) {
        if (value == null || !value.isObject()) {
            throw new ModelGatewayException("SCHEMA_INVALID", field + " must be a JSON object");
        }
        return value;
    }
}
