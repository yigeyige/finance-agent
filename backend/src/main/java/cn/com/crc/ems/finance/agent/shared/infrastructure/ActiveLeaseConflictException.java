package cn.com.crc.ems.finance.agent.shared.infrastructure;

/**
 *
 * @Description: 表示同一任务已经存在未释放活动租约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 22:20:00
 */
public final class ActiveLeaseConflictException extends RepositoryConcurrencyException {

    public ActiveLeaseConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
