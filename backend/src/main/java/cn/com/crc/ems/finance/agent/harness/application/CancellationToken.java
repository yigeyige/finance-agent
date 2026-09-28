package cn.com.crc.ems.finance.agent.harness.application;

/** 模型调用取消信号，避免网关绑定具体 HTTP 或线程实现。 */
@FunctionalInterface
public interface CancellationToken {

    boolean isCancelled();

    static CancellationToken neverCancelled() {
        return () -> false;
    }
}
