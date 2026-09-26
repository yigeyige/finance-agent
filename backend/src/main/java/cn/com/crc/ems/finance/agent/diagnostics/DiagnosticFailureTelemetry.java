package cn.com.crc.ems.finance.agent.diagnostics;

import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;

/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:32
 */
final class DiagnosticFailureTelemetry {

    private static final Pattern SQL_STATE = Pattern.compile("[A-Z0-9]{5}");

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @author: yigeyige99
     */
    private DiagnosticFailureTelemetry() {
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: logger 输入参数。
     * @param: component 输入参数。
     * @param: exception 输入参数。
     * @author: yigeyige99
     */
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

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: exception 输入参数。
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    private static String databaseState(Exception exception) {
        if (exception instanceof SQLException sqlException) {
            String state = sqlException.getSQLState();
            return state != null && SQL_STATE.matcher(state).matches() ? state : "not_provided";
        }
        return "not_applicable";
    }
}
