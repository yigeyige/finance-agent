package cn.com.crc.ems.finance.agent.retention;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RetentionRunLogTest {
    @TempDir Path root;

    @Test
    void readsOnlyRecentStructuredCleanupEntries() throws Exception {
        Files.writeString(root.resolve("cleanup.log"),
                "not-json\n"
                + "{\"startedAt\":\"2026-11-01T00:00:00Z\",\"finishedAt\":\"2026-11-01T00:01:00Z\",\"status\":\"成功\",\"deleted\":{\"task.investigation_task\":2},\"workspaceFiles\":0,\"objectFiles\":0,\"errors\":[]}\n"
                + "{\"startedAt\":\"2026-11-02T00:00:00Z\",\"finishedAt\":\"2026-11-02T00:01:00Z\",\"status\":\"失败\",\"deleted\":{},\"workspaceFiles\":0,\"objectFiles\":0,\"errors\":[\"database:SQLException\"]}\n");
        Files.writeString(root.resolve("application.log"), "private application output");

        List<RetentionRunLog.Entry> entries = new RetentionRunLog(root).recent();

        assertThat(entries).hasSize(2);
        assertThat(entries.get(0).startedAt()).isEqualTo("2026-11-02T00:00:00Z");
        assertThat(entries.get(1).deleted()).containsEntry("task.investigation_task", 2L);
    }
}
