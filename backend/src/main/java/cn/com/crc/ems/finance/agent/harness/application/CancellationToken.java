package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 模型调用取消信号，避免网关绑定具体 HTTP 或线程实现。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@FunctionalInterface
public interface CancellationToken {

    boolean isCancelled();

    static CancellationToken neverCancelled() {
        return () -> false;
    }
}
