package cn.com.crc.ems.finance.agent.diagnostics;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;

class StubHealthIndicatorTest {

    @Test
    void reportsConfiguredStubsAsUp() {
        FinanceAgentProperties properties = new FinanceAgentProperties();
        properties.getDeepSeek().setStub(true);
        properties.getSource().setStub(true);

        assertThat(new ModelStubHealthIndicator(properties).check().status()).isEqualTo("UP");
        assertThat(new SourceStubHealthIndicator(properties).check().status()).isEqualTo("UP");
    }

    @Test
    void reportsUnimplementedRealAdaptersAsUnknown() {
        FinanceAgentProperties properties = new FinanceAgentProperties();

        assertThat(new ModelStubHealthIndicator(properties).check().status()).isEqualTo("UNKNOWN");
        assertThat(new SourceStubHealthIndicator(properties).check().status()).isEqualTo("UNKNOWN");
    }
}
