package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 受控 Workspace 的安全、配额和生命周期异常。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:00:00
 */
public class ControlledWorkspaceException extends RuntimeException {

    public ControlledWorkspaceException(String message) {
        super(message);
    }

    public ControlledWorkspaceException(String message, Throwable cause) {
        super(message, cause);
    }
}
