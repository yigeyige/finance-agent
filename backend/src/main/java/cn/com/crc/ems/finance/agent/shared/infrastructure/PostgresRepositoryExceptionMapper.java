package cn.com.crc.ems.finance.agent.shared.infrastructure;

import java.sql.SQLException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.PessimisticLockingFailureException;

/**
 *
 * @Description: 将 PostgreSQL/MyBatis 数据访问异常映射为稳定的仓储异常，不暴露 SQL 细节。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 22:20:00
 */
public final class PostgresRepositoryExceptionMapper {

    private PostgresRepositoryExceptionMapper() {
    }

    /**
     *
     * @description: 映射通用数据库约束、死锁和序列化失败。
     *
     * @param: operation 操作标识，不包含 SQL 或敏感数据
     * @param: failure 原始数据访问异常
     * @return: 对外稳定的仓储异常；未知异常原样返回
     */
    public static RuntimeException map(String operation, RuntimeException failure) {
        if (isRetryable(failure)) {
            return new RetryableRepositoryException(operation + " failed and may be retried", failure);
        }
        if (failure instanceof DuplicateKeyException || failure instanceof DataIntegrityViolationException) {
            return new RepositoryConstraintViolationException(operation + " violated a database constraint", failure);
        }
        return failure;
    }

    /**
     *
     * @description: 将活动租约唯一键冲突映射为专用并发异常。
     *
     * @param: operation 操作标识
     * @param: failure 原始数据访问异常
     * @return: 活动租约冲突、可重试异常或通用约束异常
     */
    public static RuntimeException mapLeaseAcquisition(String operation, RuntimeException failure) {
        if (isRetryable(failure)) {
            return new RetryableRepositoryException(operation + " failed and may be retried", failure);
        }
        if (failure instanceof DuplicateKeyException || hasSqlState(failure, "23505")) {
            return new ActiveLeaseConflictException("active lease already exists", failure);
        }
        return map(operation, failure);
    }

    private static boolean isRetryable(Throwable failure) {
        return failure instanceof PessimisticLockingFailureException
                || hasSqlState(failure, "40P01") || hasSqlState(failure, "40001");
    }

    private static boolean hasSqlState(Throwable failure, String expected) {
        Throwable current = failure;
        while (current != null) {
            if (current instanceof SQLException sqlException && expected.equals(sqlException.getSQLState())) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
