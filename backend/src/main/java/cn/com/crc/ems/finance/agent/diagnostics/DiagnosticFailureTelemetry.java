package cn.com.crc.ems.finance.agent.diagnostics;

import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;

/** Emits bounded diagnostic failure telemetry without logging raw exception content. */
final class DiagnosticFailureTelemetry {

    private static final Pattern SQL_STATE = Pattern.compile("[A-Z0-9]{5}");

    private DiagnosticFailureTelemetry() {
    }

    static void log(Logger logger, String component, Exception exception) {
        Map<String, String> fields = DiagnosticLogSanitizer.sanitize(Map.of(
                "event", "diagnostic_probe_failed",
                "component", component,
                "outcome", "failure",
                "exceptionType", exception.getClass().getSimpleName(),
                "databaseState", databaseState(exception)));
        logger.atWarn()
                .addKeyValue("event", fields.get("event"))
                .addKeyValue("component", fields.get("component"))
                .addKeyValue("outcome", fields.get("outcome"))
                .addKeyValue("exceptionType", fields.get("exceptionType"))
                .addKeyValue("databaseState", fields.get("databaseState"))
                .log("Diagnostic probe failed");
    }

    private static String databaseState(Exception exception) {
        if (exception instanceof SQLException sqlException) {
            String state = sqlException.getSQLState();
            return state != null && SQL_STATE.matcher(state).matches() ? state : "not_provided";
        }
        return "not_applicable";
    }
}
