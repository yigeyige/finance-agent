package cn.com.crc.ems.finance.agent.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HexFormat;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cn.com.crc.ems.finance.agent.storage.application.ObjectStorageLimitExceededException;
import cn.com.crc.ems.finance.agent.storage.application.ObjectStorageNotFoundException;
import cn.com.crc.ems.finance.agent.storage.application.ObjectStoragePathViolationException;
import cn.com.crc.ems.finance.agent.storage.domain.ObjectReference;
import cn.com.crc.ems.finance.agent.storage.infrastructure.LocalTemporaryObjectStorageGateway;

class LocalTemporaryObjectStorageGatewayTest {

    @TempDir
    Path tempDir;

    @Test
    void storesDigestAndReopensByPathFreeReference() throws Exception {
        byte[] content = "hello object".getBytes(StandardCharsets.UTF_8);
        LocalTemporaryObjectStorageGateway gateway = new LocalTemporaryObjectStorageGateway(tempDir, 1024);

        ObjectReference reference = gateway.put("task-1/export.json", new ByteArrayInputStream(content),
                "application/json");

        assertThat(reference.objectKey()).isEqualTo("task-1/export.json");
        assertThat(reference.sha256()).isEqualTo(HexFormat.of().formatHex(java.security.MessageDigest
                .getInstance("SHA-256").digest(content)));
        assertThat(reference.sizeBytes()).isEqualTo(content.length);
        assertThat(reference.mediaType()).isEqualTo("application/json");
        assertThat(reference.objectKey()).doesNotContain(tempDir.toString());
        try (var input = gateway.open(reference)) {
            assertThat(input.readAllBytes()).isEqualTo(content);
        }
        assertThat(Files.exists(tempDir.resolve("task-1/export.json"))).isTrue();
    }

    @Test
    void rejectsTraversalAndLeavesNoOutsideFile() {
        LocalTemporaryObjectStorageGateway gateway = new LocalTemporaryObjectStorageGateway(tempDir, 1024);
        String[] invalidKeys = {"../escape.txt", "/escape.txt", "C:/escape.txt", "a\\b.txt", "a/../b.txt"};

        for (String key : invalidKeys) {
            assertThatThrownBy(() -> gateway.put(key, new ByteArrayInputStream(new byte[]{1}), "application/octet-stream"))
                    .isInstanceOf(ObjectStoragePathViolationException.class);
        }
        assertThat(Files.exists(tempDir.getParent().resolve("escape.txt"))).isFalse();
    }

    @Test
    void rejectsOversizedContentAndCleansTemporaryFile() throws Exception {
        LocalTemporaryObjectStorageGateway gateway = new LocalTemporaryObjectStorageGateway(tempDir, 3);

        assertThatThrownBy(() -> gateway.put("too-large.bin", new ByteArrayInputStream(new byte[]{1, 2, 3, 4}),
                "application/octet-stream")).isInstanceOf(ObjectStorageLimitExceededException.class);
        try (Stream<Path> files = Files.list(tempDir)) {
            assertThat(files).noneMatch(path -> path.getFileName().toString().startsWith(".upload-"));
        }
    }

    @Test
    void missingObjectUsesTypedFailure() {
        LocalTemporaryObjectStorageGateway gateway = new LocalTemporaryObjectStorageGateway(tempDir, 1024);
        ObjectReference reference = new ObjectReference("missing.txt", "a".repeat(64), 0, "text/plain");

        assertThatThrownBy(() -> gateway.open(reference)).isInstanceOf(ObjectStorageNotFoundException.class);
    }
}
