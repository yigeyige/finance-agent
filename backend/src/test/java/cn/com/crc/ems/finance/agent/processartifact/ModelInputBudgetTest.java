package cn.com.crc.ems.finance.agent.processartifact;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.processartifact.domain.ModelInputBudget;

/** 验证模型输入在调用前按实际模型名执行保守字节门禁。 */
class ModelInputBudgetTest {

    @Test
    void rejectsOversizedUnknownModelInputBeforeCall() {
        assertThatThrownBy(() -> ModelInputBudget.requireWithinLimit("未知模型", "测".repeat(30_000)))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("缩减材料");
    }

    @Test
    void acceptsSmallInputForKnownModel() {
        ModelInputBudget.requireWithinLimit("deepseek-flash", "流程调查输入");
    }

    @Test
    void currentModelAllowsHalfOfOneMillionInputBytes() {
        assertThatCode(() -> ModelInputBudget.requireWithinLimit("deepseek-v4.1-flash", "x".repeat(500_000)))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> ModelInputBudget.requireWithinLimit("deepseek-v4.1-flash", "x".repeat(500_001)))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("缩减材料");
    }
}
