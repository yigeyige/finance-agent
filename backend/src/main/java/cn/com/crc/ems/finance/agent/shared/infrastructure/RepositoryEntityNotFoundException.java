package cn.com.crc.ems.finance.agent.shared.infrastructure;

/**
 *
 * @Description: 表示事务边界要求的数据库实体不存在。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 22:20:00
 */
public final class RepositoryEntityNotFoundException extends RuntimeException {

    public RepositoryEntityNotFoundException(String message) {
        super(message);
    }
}
