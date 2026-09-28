package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import cn.com.crc.ems.finance.agent.harness.application.ControlledWorkspaceException;
import cn.com.crc.ems.finance.agent.harness.application.WorkspaceArtifact;
import cn.com.crc.ems.finance.agent.harness.infrastructure.ControlledWorkspaceManager;

class ControlledWorkspaceManagerTest {

    @TempDir
    Path root;

    @Test
    void isolatesSameRelativePathAcrossUsersAndTasks() throws IOException {
        ControlledWorkspaceManager manager = manager(128, 64);
        UUID userA = UUID.randomUUID();
        UUID userB = UUID.randomUUID();
        UUID taskA = UUID.randomUUID();
        UUID taskB = UUID.randomUUID();
        UUID session = UUID.randomUUID();

        WorkspaceArtifact first = manager.write(userA, taskA, session, "payload/data.json",
                content("first"), "application/json");
        WorkspaceArtifact second = manager.write(userB, taskB, session, "payload/data.json",
                content("second"), "application/json");

        assertThat(first.path()).isNotEqualTo(second.path());
        assertThat(manager.workspacePath(userA, taskA, session)).isNotEqualTo(manager.workspacePath(userB, taskB, session));
        try (var input = manager.open(userA, taskA, session, "payload/data.json")) {
            assertThat(new String(input.readAllBytes(), StandardCharsets.UTF_8)).isEqualTo("first");
        }
        assertThat(manager.usage(userA, taskA, session)).isEqualTo(5);
    }

    @Test
    void rejectsAbsoluteTraversalAndDisallowedMediaType() {
        ControlledWorkspaceManager manager = manager(128, 64);
        UUID user = UUID.randomUUID();
        UUID task = UUID.randomUUID();
        UUID session = UUID.randomUUID();

        assertThatThrownBy(() -> manager.write(user, task, session, root.resolve("outside.txt").toString(),
                content("x"), "text/plain")).isInstanceOf(ControlledWorkspaceException.class);
        assertThatThrownBy(() -> manager.write(user, task, session, "../outside.txt", content("x"), "text/plain"))
                .isInstanceOf(ControlledWorkspaceException.class);
        assertThatThrownBy(() -> manager.write(user, task, session, "payload.txt", content("x"), "image/png"))
                .isInstanceOf(ControlledWorkspaceException.class);
    }

    @Test
    void rejectsSymlinkEscapeWhenPlatformSupportsSymlinks() throws IOException {
        ControlledWorkspaceManager manager = manager(128, 64);
        UUID user = UUID.randomUUID();
        UUID task = UUID.randomUUID();
        UUID session = UUID.randomUUID();
        Path outside = root.resolve("outside");
        Files.createDirectories(outside);
        Path link = manager.workspacePath(user, task, session).resolve("linked");
        Files.createDirectories(link.getParent());
        try {
            Files.createSymbolicLink(link, outside);
        } catch (UnsupportedOperationException | SecurityException | IOException exception) {
            Assumptions.assumeTrue(false, "symbolic links are unavailable on this platform");
        }

        assertThatThrownBy(() -> manager.write(user, task, session, "linked/data.txt", content("x"), "text/plain"))
                .isInstanceOf(ControlledWorkspaceException.class);
    }

    @Test
    void enforcesFileAndWorkspaceQuotasAndLeavesNoPartialUpload() throws IOException {
        ControlledWorkspaceManager manager = manager(10, 8);
        UUID user = UUID.randomUUID();
        UUID task = UUID.randomUUID();
        UUID session = UUID.randomUUID();

        assertThatThrownBy(() -> manager.write(user, task, session, "too-large.txt", content("123456789"), "text/plain"))
                .isInstanceOf(ControlledWorkspaceException.class);
        manager.write(user, task, session, "one.txt", content("12345678"), "text/plain");
        assertThatThrownBy(() -> manager.write(user, task, session, "two.txt", content("12345678"), "text/plain"))
                .isInstanceOf(ControlledWorkspaceException.class);
        try (var files = Files.list(manager.workspacePath(user, task, session))) {
            assertThat(files.filter(path -> path.getFileName().toString().startsWith(".upload-")).toList()).isEmpty();
        }
    }

    @Test
    void cleanupRemovesOnlyCompleteWorkspaceBoundary() throws IOException {
        ControlledWorkspaceManager manager = manager(128, 64);
        UUID user = UUID.randomUUID();
        UUID task = UUID.randomUUID();
        UUID sessionA = UUID.randomUUID();
        UUID sessionB = UUID.randomUUID();
        manager.write(user, task, sessionA, "a.txt", content("a"), "text/plain");
        manager.write(user, task, sessionB, "b.txt", content("b"), "text/plain");

        manager.cleanup(user, task, sessionA);

        assertThat(Files.exists(manager.workspacePath(user, task, sessionA))).isFalse();
        assertThat(Files.exists(manager.workspacePath(user, task, sessionB).resolve("b.txt"))).isTrue();
    }

    private ControlledWorkspaceManager manager(long maxBytes, long maxFileBytes) {
        return new ControlledWorkspaceManager(root, maxBytes, maxFileBytes,
                Set.of("application/json", "text/plain", "text/csv"));
    }

    private static java.io.InputStream content(String value) {
        return new java.io.ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }
}
