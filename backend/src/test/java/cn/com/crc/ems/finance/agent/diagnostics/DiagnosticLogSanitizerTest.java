package cn.com.crc.ems.finance.agent.diagnostics;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

class DiagnosticLogSanitizerTest {

    @Test
    void keepsApprovedFieldsAndRedactsForbiddenKeysAndValues() {
        Map<String, String> result = DiagnosticLogSanitizer.sanitize(Map.of(
                "event", "diagnostics_checked",
                "status", "UP",
                "password", "do-not-print",
                "prompt", "full prompt text",
                "outcome", "bearer secret-token"));

        assertThat(result).containsEntry("event", "diagnostics_checked")
                .containsEntry("status", "UP")
                .containsEntry("password", DiagnosticLogSanitizer.REDACTED)
                .containsEntry("prompt", DiagnosticLogSanitizer.REDACTED)
                .containsEntry("outcome", DiagnosticLogSanitizer.REDACTED)
                .doesNotContainValue("do-not-print")
                .doesNotContainValue("full prompt text");
    }

    @Test
    void normalizesUnknownStatus() {
        assertThat(DiagnosticLogSanitizer.safeStatus("broken")).isEqualTo("UNKNOWN");
        assertThat(DiagnosticLogSanitizer.safeStatus("down")).isEqualTo("DOWN");
    }
}
