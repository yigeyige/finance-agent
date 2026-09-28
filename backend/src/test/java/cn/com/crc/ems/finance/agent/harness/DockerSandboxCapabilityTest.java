package cn.com.crc.ems.finance.agent.harness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.harness.application.DockerFilesystemSpec;
import cn.com.crc.ems.finance.agent.harness.application.DockerSandboxCapability;
import cn.com.crc.ems.finance.agent.harness.application.DockerSandboxCapabilityProbe;
import cn.com.crc.ems.finance.agent.harness.application.DockerSandboxProfile;
import cn.com.crc.ems.finance.agent.harness.application.SyntheticSandboxComputation;

class DockerSandboxCapabilityTest {

    @Test
    void jarLocalSpecIsBoundedAndApplicationContainerIsSocketFree() {
        DockerFilesystemSpec local = DockerFilesystemSpec.jarLocal();
        DockerFilesystemSpec application = DockerFilesystemSpec.applicationContainer();

        assertThat(local.enabled()).isTrue();
        assertThat(local.networkEnabled()).isFalse();
        assertThat(local.mounts()).isEmpty();
        assertThat(local.dockerSocketMountAllowed()).isFalse();
        assertThat(local.cpuLimitMillis()).isPositive();
        assertThat(local.memoryLimitBytes()).isPositive();
        assertThat(local.timeoutMillis()).isPositive();
        assertThat(application.enabled()).isFalse();
        assertThat(application.disabledReason()).contains("does not mount Docker socket");
    }

    @Test
    void probeDoesNotStartDockerAndReportsContainerNotApplicable() {
        DockerSandboxCapabilityProbe probe = new DockerSandboxCapabilityProbe();

        assertThat(probe.probe(DockerSandboxProfile.JAR_LOCAL, true).status())
                .isEqualTo(DockerSandboxCapability.Status.AVAILABLE);
        assertThat(probe.probe(DockerSandboxProfile.JAR_LOCAL, false).status())
                .isEqualTo(DockerSandboxCapability.Status.UNAVAILABLE);
        DockerSandboxCapability application = probe.probe(DockerSandboxProfile.APPLICATION_CONTAINER, true);
        assertThat(application.status()).isEqualTo(DockerSandboxCapability.Status.NOT_APPLICABLE);
        assertThat(application.reason()).contains("Docker socket");
    }

    @Test
    void syntheticComputationIsStableAndBounded() {
        SyntheticSandboxComputation computation = new SyntheticSandboxComputation();
        assertThat(computation.calculate(2, 3)).isEqualTo(computation.calculate(2, 3));
        assertThat(computation.calculate(2, 3).value()).isEqualTo(5);
        assertThatThrownBy(() -> computation.calculate(1_000_001, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deploymentFilesAreNonRootAndDoNotRequireSocketOrCompose() throws IOException {
        Path deployment = Path.of("..", "deploy");
        String backend = Files.readString(deployment.resolve("backend.Dockerfile"));
        String frontend = Files.readString(deployment.resolve("frontend.Dockerfile"));
        assertThat(backend).contains("USER 10001:10001").doesNotContain("docker.sock");
        assertThat(frontend).contains("USER nginx").doesNotContain("docker.sock");
        try (Stream<Path> files = Files.list(deployment)) {
            assertThat(files.map(path -> path.getFileName().toString().toLowerCase())
                    .filter(name -> name.contains("compose")).toList()).isEmpty();
        }
    }
}
