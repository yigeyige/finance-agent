package cn.com.crc.ems.finance.agent.harness.application;

import java.util.Optional;

/**
 * 当前模型/工具循环的易失 reasoning 容器；不实现序列化，关闭后立即清空内容。
 */
public final class EphemeralThinkingContext implements AutoCloseable {

    private String reasoningContent;
    private boolean closed;

    /**
     * 暂存当前循环的 reasoning，禁止在关闭后重新写入。
     *
     * @param reasoningContent 当前循环 reasoning 内容。
     * @throws IllegalStateException 容器已经关闭时抛出。
     * @author yigeyige99
     */
    public void capture(String reasoningContent) {
        ensureOpen();
        this.reasoningContent = reasoningContent;
    }

    public Optional<String> reasoningContent() {
        return closed ? Optional.empty() : Optional.ofNullable(reasoningContent);
    }

    public boolean isClosed() {
        return closed;
    }

    /** 清空 reasoning 并使容器不可复用。 */
    @Override
    public void close() {
        reasoningContent = null;
        closed = true;
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("thinking context is closed");
        }
    }
}
