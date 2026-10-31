package cn.com.crc.ems.finance.agent.emsdbconfig.application;

/**
 * @Description: 手动同步失败时使用的安全业务异常，不向页面暴露源库或持久化细节。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 22:00:00
 */
public final class EmsSchemaSyncException extends RuntimeException {
    public EmsSchemaSyncException(Throwable cause) {
        super("数据库实例结构同步失败，原目录已保留", cause);
    }
}
