package cn.com.crc.ems.finance.agent.retention;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RetentionJobTest {
    @TempDir Path temporary;

    @Test
    void runRemovesExpiredOwnedFilesAndRecordsResult() throws Exception {
        Path workspace = temporary.resolve("workspace");
        Path objects = Files.createDirectories(workspace.resolve("objects"));
        Path oldObject = Files.writeString(objects.resolve("old.bin"), "old");
        Files.setLastModifiedTime(oldObject, FileTime.from(Instant.parse("2026-09-01T00:00:00Z")));
        Path localLogs = Files.createDirectories(temporary.resolve("local-logs"));
        Path oldLog = Files.writeString(localLogs.resolve("backend-20260901-000000.20260901.000.log"), "old");
        Files.setLastModifiedTime(oldLog, FileTime.from(Instant.parse("2026-09-01T00:00:00Z")));
        RetentionDatabaseCleaner cleaner = new RetentionDatabaseCleaner(new EmptyRepository(), 500);
        ArrayList<RetentionJob.Run> logged = new ArrayList<>();
        Clock clock = Clock.fixed(Instant.parse("2026-11-04T00:00:00Z"), ZoneOffset.UTC);
        RetentionJob job = new RetentionJob(cleaner, clock, workspace, objects, localLogs, true, logged::add);

        RetentionJob.Run result = job.execute();

        assertThat(oldObject).doesNotExist();
        assertThat(oldLog).doesNotExist();
        assertThat(result.objectFiles()).isEqualTo(1);
        assertThat(result.localLogFiles()).isEqualTo(1);
        assertThat(result.cutoff()).isEqualTo(Instant.parse("2026-10-05T00:00:00Z"));
        assertThat(result.errors()).isEmpty();
        assertThat(logged).containsExactly(result);
    }

    private static final class EmptyRepository implements RetentionDatabaseCleaner.Repository {
        @Override public Map<String, Long> countAll() {
            Map<String, Long> counts = new HashMap<>();
            for (RetentionCatalog.Table table : RetentionCatalog.tables()) counts.put(table.fullName(), 0L);
            return counts;
        }
        @Override public int deleteBatch(RetentionCatalog.Table table, Instant cutoff, int limit) { return 0; }
    }
}
