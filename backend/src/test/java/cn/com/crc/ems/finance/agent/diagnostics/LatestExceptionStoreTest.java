package cn.com.crc.ems.finance.agent.diagnostics;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

/**
 * @Description: 验证最近异常内存快照只保留安全堆栈与最后一次失败。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-02 23:00:00
 */
class LatestExceptionStoreTest {
    @Test
    void replacesPreviousSnapshotWithoutRetainingExceptionMessages() {
        var store = new LatestExceptionStore(Clock.fixed(Instant.parse("2026-11-02T00:00:00Z"), ZoneOffset.UTC));
        assertThat(store.latest()).isNull();

        store.record(LatestExceptionStore.Source.BUSINESS_API,
                new IllegalStateException("password=secret; 用户张三; prompt=hidden-prompt-123"));
        var first = store.latest();
        assertThat(first).isNotNull();
        assertThat(first.occurredAt()).isEqualTo(Instant.parse("2026-11-02T00:00:00Z"));
        assertThat(first.stackTrace()).contains("LatestExceptionStoreTest");
        assertThat(first.stackTrace()).doesNotContain("secret", "张三", "hidden-prompt-123", "password=", "prompt=");

        store.record(LatestExceptionStore.Source.MODEL_INTERACTION,
                new RuntimeException("Bearer token-123", new IllegalArgumentException("jdbc:postgresql://secret")));
        var latest = store.latest();
        assertThat(latest.source()).isEqualTo(LatestExceptionStore.Source.MODEL_INTERACTION);
        assertThat(latest.exceptionType()).isEqualTo(RuntimeException.class.getName());
        assertThat(latest.stackTrace()).contains(IllegalArgumentException.class.getName());
        assertThat(latest.stackTrace()).doesNotContain("token-123", "jdbc:postgresql");
        assertThat(latest.stackTrace().length()).isLessThanOrEqualTo(16_000);
    }

    @Test
    void boundsDeepAndLargeStackChains() {
        var store = new LatestExceptionStore(Clock.systemUTC());
        Throwable failure = new IllegalStateException("sensitive message");
        for (int index = 0; index < 20; index++) {
            failure = new RuntimeException("secret-" + index, failure);
        }
        store.record(LatestExceptionStore.Source.MODEL_INTERACTION, failure);

        assertThat(store.latest().stackTrace().length()).isLessThanOrEqualTo(16_000);
        assertThat(store.latest().stackTrace()).doesNotContain("secret-");
    }
}
