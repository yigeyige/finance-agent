package cn.com.crc.ems.finance.agent.config;

import java.util.function.Supplier;

/** API 与 Worker 数据源路由的线程上下文，默认使用 API 连接池。 */
public final class DataSourceRoleContext {

    private static final ThreadLocal<Role> CURRENT = new ThreadLocal<>();

    private DataSourceRoleContext() {
    }

    /** 在 Worker 连接池上下文执行回调，并保证 finally 清理线程变量。 */
    public static <T> T withWorker(Supplier<T> action) {
        Role previous = CURRENT.get();
        CURRENT.set(Role.WORKER);
        try {
            return action.get();
        } finally {
            restore(previous);
        }
    }

    /** 返回当前线程应使用的数据源角色。 */
    public static Role currentRole() {
        return CURRENT.get() == null ? Role.API : CURRENT.get();
    }

    private static void restore(Role previous) {
        if (previous == null) {
            CURRENT.remove();
        } else {
            CURRENT.set(previous);
        }
    }

    /** 数据源角色。 */
    public enum Role {
        API,
        WORKER
    }
}
