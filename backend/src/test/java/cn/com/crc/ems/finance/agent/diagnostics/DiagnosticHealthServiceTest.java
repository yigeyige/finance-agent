package cn.com.crc.ems.finance.agent.diagnostics;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class DiagnosticHealthServiceTest {

    @Test
    void reportsDownWhenAnyDependencyIsDownAndSortsComponents() {
        DiagnosticHealthService service = new DiagnosticHealthService(List.of(
                probe("sourceStub", "UP"), probe("database", "DOWN"), probe("modelStub", "UP")));

        DiagnosticHealthService.DiagnosticSnapshot snapshot = service.snapshot();

        assertThat(snapshot.status()).isEqualTo("DOWN");
        assertThat(snapshot.components().keySet()).containsExactly("database", "modelStub", "sourceStub");
    }

    private static DiagnosticProbe probe(String name, String status) {
        return new DiagnosticProbe() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public DiagnosticComponentStatus check() {
                return new DiagnosticComponentStatus(name, status, "test");
            }
        };
    }
}
