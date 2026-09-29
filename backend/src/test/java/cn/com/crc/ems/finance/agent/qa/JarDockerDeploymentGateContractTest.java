package cn.com.crc.ems.finance.agent.qa;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

/** 校验 JAR、前后端独立镜像和外部 PostgreSQL 部署契约。 */
class JarDockerDeploymentGateContractTest {

    @Test
    void deploymentCheckAndGuideDeclareExternalDatabaseAndHealthFlow() throws IOException {
        String script = readRepoFile("scripts/qa-deployment-artifacts.ps1");
        String guide = readRepoFile("docs/deployment-checklist.md");
        String prod = readRepoFile("backend/src/main/resources/application-prod.yml");
        assertThat(script).contains("BuildArtifacts", "FINANCE_AGENT_DB_URL", "Docker socket", "compose=0",
                "health=/actuator/health");
        assertThat(guide).contains("FINANCE_AGENT_DB_PASSWORD", "/actuator/health/readiness", "docker stop")
                .doesNotContain("docker-compose", "docker compose");
        assertThat(prod).contains("FINANCE_AGENT_DB_URL", "FINANCE_AGENT_DB_USERNAME", "FINANCE_AGENT_DB_PASSWORD");
    }

    @Test
    void deploymentFilesStayNonRootAndSocketFree() throws IOException {
        String backend = readRepoFile("deploy/backend.Dockerfile");
        String frontend = readRepoFile("deploy/frontend.Dockerfile");
        String nginx = readRepoFile("deploy/nginx.conf");
        assertThat(backend).contains("WORKDIR ${APP_HOME}", "EXPOSE 8080", "USER 10001:10001")
                .doesNotContain("docker.sock");
        assertThat(frontend).contains("EXPOSE 8080", "USER nginx").doesNotContain("docker.sock");
        assertThat(nginx).contains("listen 8080", "try_files");
    }

    private static String readRepoFile(String relativePath) throws IOException {
        return Files.readString(Path.of("..", relativePath), StandardCharsets.UTF_8);
    }
}
