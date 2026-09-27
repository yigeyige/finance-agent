package cn.com.crc.ems.finance.agent.shared.infrastructure;

/**
 *
 * @Description: 表示乐观版本或其他并发条件不再满足的仓储操作失败。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 22:20:00
 */
public final class ConcurrentRepositoryOperationException extends RepositoryConcurrencyException {

    public ConcurrentRepositoryOperationException(String message) {
        super(message);
    }
}
