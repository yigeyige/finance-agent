package cn.com.crc.ems.finance.agent.shared.infrastructure;

/**
 *
 * @Description: 表示 PostgreSQL 死锁或序列化失败等可由上层策略重试的仓储异常。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 22:20:00
 */
public final class RetryableRepositoryException extends RuntimeException {

    public RetryableRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
