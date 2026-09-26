package cn.com.crc.ems.finance.agent.diagnostics;

/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:29
 */
public record DiagnosticComponentStatus(String name, String status, String reason) {

    public DiagnosticComponentStatus {
        if (name == null || name.isBlank() || status == null || status.isBlank()) {
            throw new IllegalArgumentException("name and status are required");
        }
        reason = reason == null || reason.isBlank() ? "not_provided" : reason;
    }
}
