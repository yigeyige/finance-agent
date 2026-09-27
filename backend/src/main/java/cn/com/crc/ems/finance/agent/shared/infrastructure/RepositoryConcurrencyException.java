package cn.com.crc.ems.finance.agent.shared.infrastructure;

/**
 *
 * @Description: 表示仓储读写发生并发冲突，调用方必须重新读取权威状态。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 22:20:00
 */
public class RepositoryConcurrencyException extends RuntimeException {

    public RepositoryConcurrencyException(String message) {
        super(message);
    }

    public RepositoryConcurrencyException(String message, Throwable cause) {
        super(message, cause);
    }
}
