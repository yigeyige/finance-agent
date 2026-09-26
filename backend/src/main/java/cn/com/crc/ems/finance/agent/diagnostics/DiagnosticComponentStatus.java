package cn.com.crc.ems.finance.agent.diagnostics;

/** Safe status returned by a dependency probe; it intentionally carries no raw errors or configuration values. */
public record DiagnosticComponentStatus(String name, String status, String reason) {

    public DiagnosticComponentStatus {
        if (name == null || name.isBlank() || status == null || status.isBlank()) {
            throw new IllegalArgumentException("name and status are required");
        }
        reason = reason == null || reason.isBlank() ? "not_provided" : reason;
    }
}
