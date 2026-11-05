package cn.com.crc.ems.finance.agent.retention;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RetentionFileCleanerTest {
    @TempDir Path temporary;

    @Test
    void workspaceCleanupRequiresThreeUuidIdentitySegmentsAndExcludesObjects() throws Exception {
        Path root = temporary.resolve("workspace");
        Path session = root.resolve("11111111-1111-4111-8111-111111111111")
                .resolve("22222222-2222-4222-8222-222222222222")
                .resolve("33333333-3333-4333-8333-333333333333");
        Files.createDirectories(session);
        Path old = Files.writeString(session.resolve("old.txt"), "old");
        Path current = Files.writeString(session.resolve("current.txt"), "current");
        Path unrelated = Files.writeString(root.resolve("unrelated.txt"), "keep");
        Path object = Files.writeString(Files.createDirectories(root.resolve("objects")).resolve("old.txt"), "old");
        Files.setLastModifiedTime(old, FileTime.from(Instant.parse("2026-09-01T00:00:00Z")));
        Files.setLastModifiedTime(object, FileTime.from(Instant.parse("2026-09-01T00:00:00Z")));

        long deleted = RetentionFileCleaner.cleanWorkspace(root, root.resolve("objects"),
                Instant.parse("2026-10-05T00:00:00Z"));

        assertThat(deleted).isEqualTo(1);
        assertThat(old).doesNotExist();
        assertThat(current).exists();
        assertThat(unrelated).exists();
        assertThat(object).exists();
    }

    @Test
    void objectCleanupDeletesExpiredFilesIncludingAbandonedTemporaryUploads() throws Exception {
        Path root = Files.createDirectories(temporary.resolve("objects"));
        Path old = Files.writeString(root.resolve("object.bin"), "old");
        Path upload = Files.writeString(root.resolve(".upload-running.tmp"), "ongoing");
        Files.setLastModifiedTime(old, FileTime.from(Instant.parse("2026-09-01T00:00:00Z")));
        Files.setLastModifiedTime(upload, FileTime.from(Instant.parse("2026-09-01T00:00:00Z")));

        long deleted = RetentionFileCleaner.cleanObjects(root, Instant.parse("2026-10-05T00:00:00Z"));

        assertThat(deleted).isEqualTo(2);
        assertThat(old).doesNotExist();
        assertThat(upload).doesNotExist();
    }

    @Test
    void localLogCleanupOnlyDeletesOwnedExpiredFiles() throws Exception {
        Path root = Files.createDirectories(temporary.resolve("local-logs"));
        Path old = Files.writeString(root.resolve("backend-20260901-000000.20260901.000.log"), "old");
        Path current = Files.writeString(root.resolve("frontend-20261104-000000.20261104.000.log"), "new");
        Path unrelated = Files.writeString(root.resolve("notes.log"), "keep");
        Files.setLastModifiedTime(old, FileTime.from(Instant.parse("2026-09-01T00:00:00Z")));
        Files.setLastModifiedTime(unrelated, FileTime.from(Instant.parse("2026-09-01T00:00:00Z")));

        long deleted = RetentionFileCleaner.cleanLocalLogs(root, Instant.parse("2026-10-05T00:00:00Z"));

        assertThat(deleted).isEqualTo(1);
        assertThat(old).doesNotExist();
        assertThat(current).exists();
        assertThat(unrelated).exists();
    }
}
