package cn.com.crc.ems.finance.agent.harness.application;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

/** 离线确定性 Stub 的结构化输出 fixture。 */
public record ModelFixture(JsonNode output, int tokens, BigDecimal cost) {

    public ModelFixture {
        if (output == null || tokens < 0 || cost == null || cost.signum() < 0) {
            throw new IllegalArgumentException("model fixture is invalid");
        }
        output = output.deepCopy();
    }
}
