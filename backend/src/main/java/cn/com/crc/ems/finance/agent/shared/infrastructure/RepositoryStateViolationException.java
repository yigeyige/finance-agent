package cn.com.crc.ems.finance.agent.shared.infrastructure;

/**
 *
 * @Description: 表示仓储事务要求的领域状态守卫未满足。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-27 22:20:00
 */
public final class RepositoryStateViolationException extends IllegalStateException {

    public RepositoryStateViolationException(String message) {
        super(message);
    }
}
