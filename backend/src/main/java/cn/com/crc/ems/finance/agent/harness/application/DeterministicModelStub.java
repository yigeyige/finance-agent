package cn.com.crc.ems.finance.agent.harness.application;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;

/**
 *
 * @Description: 仅从离线 fixture 返回确定性结构化结果的模型 Stub，绝不访问网络或生成自然语言。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Component
public final class DeterministicModelStub implements ModelGateway {

    private final Map<String, ModelFixture> fixtures;
    private final String modelBindingVersion;

    public DeterministicModelStub() {
        this(Map.of(), "deterministic-stub-v1");
    }

    public DeterministicModelStub(Map<String, ModelFixture> fixtures, String modelBindingVersion) {
        this.fixtures = fixtures == null ? Map.of() : Map.copyOf(fixtures);
        if (modelBindingVersion == null || modelBindingVersion.isBlank()) {
            throw new IllegalArgumentException("modelBindingVersion must not be blank");
        }
        this.modelBindingVersion = modelBindingVersion;
    }

    /** 生成 fixture 定位键，调用方可据此注册不含原文的离线 fixture。 */
    public static String fixtureKey(ModelPurpose purpose, PromptTemplateVersion template,
            AllowedContext context) {
        ModelGateway.requireRequest(purpose, template, context, new JsonSchema<Object>() {
            @Override
            public String schemaRef() {
                return "fixture-key";
            }

            @Override
            public Object validate(JsonNode value) {
                return value;
            }
        }, new ModelBudget(1, 1, BigDecimal.ZERO, Duration.ofSeconds(1)),
                CancellationToken.neverCancelled());
        return purpose.name() + "|" + template.templateCode() + "|" + template.version()
                + "|" + context.digest();
    }

    @Override
    public <T> ModelResult<T> generateStructured(ModelPurpose purpose, PromptTemplateVersion template,
            AllowedContext context, JsonSchema<T> outputSchema, ModelBudget budget,
            CancellationToken cancellationToken) {
        ModelGateway.requireRequest(purpose, template, context, outputSchema, budget, cancellationToken);
        if (cancellationToken.isCancelled()) {
            throw new ModelGatewayException("CANCELLED", "model invocation was cancelled");
        }
        ModelFixture fixture = fixtures.get(fixtureKey(purpose, template, context));
        if (fixture == null) {
            throw new ModelGatewayException("FIXTURE_NOT_FOUND", "deterministic fixture was not found");
        }
        if (fixture.tokens() > budget.maxTokens() || fixture.cost().compareTo(budget.maxCost()) > 0) {
            throw new ModelGatewayException("BUDGET_EXHAUSTED", "model budget was exceeded");
        }
        long started = System.nanoTime();
        T value;
        try {
            value = outputSchema.validate(fixture.output().deepCopy());
        } catch (ModelGatewayException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ModelGatewayException("SCHEMA_INVALID", "model output failed schema validation");
        }
        if (value == null) {
            throw new ModelGatewayException("SCHEMA_INVALID", "model output failed schema validation");
        }
        String outputDigest = digest(CanonicalJsonCodec.canonicalize(fixture.output()));
        String inputDigest = context.digest();
        ModelMetadata metadata = new ModelMetadata(modelBindingVersion, template.version(), context.evidenceIds(),
                context.fieldDigest(), inputDigest,
                outputDigest, fixture.tokens(), Duration.ofNanos(System.nanoTime() - started).toMillis(),
                "STOP", true);
        return new ModelResult<>(value, metadata);
    }

    private static String digest(String value) {
        return Sha256Digest.hex(value);
    }
}
