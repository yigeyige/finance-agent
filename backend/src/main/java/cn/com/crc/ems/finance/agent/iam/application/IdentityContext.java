package cn.com.crc.ems.finance.agent.iam.application;

/** 保存当前请求的可信身份，并保证请求结束后清理线程状态。 */
public final class IdentityContext {

    private static final ThreadLocal<CurrentIdentity> CURRENT = new ThreadLocal<>();

    private IdentityContext() {
    }

    /** 返回当前请求身份，未解析时返回空。 */
    public static CurrentIdentity current() {
        return CURRENT.get();
    }

    /** 要求当前请求已经完成身份解析。 */
    public static CurrentIdentity require() {
        CurrentIdentity identity = current();
        if (identity == null) {
            throw new IdentityRequiredException();
        }
        return identity;
    }

    /** 写入当前请求身份。 */
    public static void set(CurrentIdentity identity) {
        CURRENT.set(identity);
    }

    /** 清理当前线程上的请求身份。 */
    public static void clear() {
        CURRENT.remove();
    }

    /** 表示请求缺少可信身份。 */
    public static final class IdentityRequiredException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
