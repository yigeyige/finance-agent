package cn.com.crc.ems.finance.agent.shared.infrastructure;

/**
 *
 * @Description: 表示数据库约束拒绝了仓储写入。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 22:20:00
 */
public final class RepositoryConstraintViolationException extends RuntimeException {

    public RepositoryConstraintViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
