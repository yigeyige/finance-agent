package cn.com.crc.ems.finance.agent.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

/** 验证应用配置固定 UTC 时钟和 Jackson API 传输约束。 */
class UtcSerializationContractTest {

    @Test
    void applicationClockUsesUtc() {
        assertThat(new UtcTimeConfiguration().applicationClock().getZone()).isEqualTo(ZoneOffset.UTC);
        assertThat(new UtcTimeConfiguration().applicationClock()).isInstanceOf(Clock.class);
    }

    @Test
    void propertiesFixUtcAndIsoSerialization() {
        assertThat(readProperties()).contains("spring.jackson.time-zone=UTC");
    }

    private String readProperties() {
        try (var stream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (stream == null) {
                throw new AssertionError("application.properties is missing");
            }
            return new String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (java.io.IOException exception) {
            throw new AssertionError("application.properties cannot be read", exception);
        }
    }
}
