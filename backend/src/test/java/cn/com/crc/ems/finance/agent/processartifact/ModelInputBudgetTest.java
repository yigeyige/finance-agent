package cn.com.crc.ems.finance.agent.processartifact;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.processartifact.domain.ModelInputBudget;

/** 验证模型输入在调用前受实际模型名与 1M 总上限约束。 */
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
}
