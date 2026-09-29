package cn.com.crc.ems.finance.agent.qa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.iam.web.PermissionGuardInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @Description: 校验审批、权限和发布负向门禁的用例覆盖与服务端角色边界。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 00:00:00
 */
class ApprovalPermissionPublicationNegativeGateContractTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();
    private static final UUID VIEWER_ID = UUID.fromString("00000000-0000-4000-8000-000000000002");
    private static final UUID OPERATOR_ID = UUID.fromString("00000000-0000-4000-8000-000000000001");

    @AfterEach
    void clearIdentity() {
        IdentityContext.clear();
    }

    @Test
    void fixtureCoversAllRequiredNegativeCasesAndEvidence() throws IOException {
        JsonNode fixture = fixture();
        assertThat(fixture.path("fixtureVersion").asText())
                .isEqualTo("approval-permission-publication-negative-v1");
        assertThat(fixture.path("cases")).hasSize(10);
        assertThat(fixture.path("evidenceRequirements")).extracting(JsonNode::asText)
                .containsExactlyInAnyOrder("response", "status", "audit");
        assertThat(fixture.toString()).doesNotContain("real-data", "production", "apiKey", "password");
    }

    @Test
    void viewerCannotMutateTaskButCanReadTaskEvents() throws Exception {
        PermissionGuardInterceptor guard = new PermissionGuardInterceptor();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getRequestURI()).thenReturn("/api/v1/tasks/00000000-0000-4000-8000-000000000301/packet-publications");
        when(request.getMethod()).thenReturn("POST");
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        IdentityContext.set(new CurrentIdentity(VIEWER_ID, "local-viewer", "本地查看员", Set.of(RoleCode.VIEWER)));
        assertThat(guard.preHandle(request, response, null)).isFalse();
        verify(response).setStatus(403);

        when(request.getRequestURI()).thenReturn("/api/tasks/00000000-0000-4000-8000-000000000301/events");
        when(request.getMethod()).thenReturn("GET");
        assertThat(guard.preHandle(request, response, null)).isTrue();
    }

    @Test
    void operatorCanEnterTaskMutationBoundary() throws Exception {
        PermissionGuardInterceptor guard = new PermissionGuardInterceptor();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getRequestURI()).thenReturn("/api/v1/tasks/00000000-0000-4000-8000-000000000301/packet-publications");
        when(request.getMethod()).thenReturn("POST");
        IdentityContext.set(new CurrentIdentity(OPERATOR_ID, "local-operator", "本地操作员", Set.of(RoleCode.OPERATOR)));
        assertThat(guard.preHandle(request, response, null)).isTrue();
    }

    @Test
    void publicationAndReviewSourcesKeepBindingAndRoleGuards() throws IOException {
        String publication = readRepoFile("backend/src/main/java/cn/com/crc/ems/finance/agent/packet/application/PacketPublicationService.java");
        String lifecycle = readRepoFile("backend/src/main/java/cn/com/crc/ems/finance/agent/packet/application/PacketLifecycleService.java");
        String publicationController = readRepoFile("backend/src/main/java/cn/com/crc/ems/finance/agent/packet/web/PacketPublicationController.java");
        String reviewController = readRepoFile("backend/src/main/java/cn/com/crc/ems/finance/agent/packet/web/PacketReviewController.java");
        String lifecycleController = readRepoFile("backend/src/main/java/cn/com/crc/ems/finance/agent/packet/web/PacketLifecycleController.java");
        assertThat(publication).contains("packet draft bindings are stale", "approved review does not match draft bindings",
                "canonical content and HTML snapshot digest do not match");
        assertThat(lifecycle).contains("packet revision is not the current published revision");
        assertThat(publicationController).contains("RoleCode.OPERATOR", "HttpStatus.FORBIDDEN");
        assertThat(reviewController).contains("RoleCode.OPERATOR", "HttpStatus.FORBIDDEN");
        assertThat(lifecycleController).contains("requireOperator", "RoleCode.OPERATOR");
    }

    @Test
    void scanScriptIsOfflineFirstAndRequiresEvidenceForExecute() throws IOException {
        String script = readRepoFile("scripts/qa-approval-permission-publication-negative-gate.ps1");
        assertThat(script).contains("-Execute", "networkCalls=0", "databaseCalls=0", "EvidenceRoot is required");
    }

    private static JsonNode fixture() throws IOException {
        return JSON.readTree(Path.of("..", "scripts", "fixtures", "approval-permission-publication-negative-v1.json")
                .toFile());
    }

    private static String readRepoFile(String relativePath) throws IOException {
        return Files.readString(Path.of("..", relativePath), StandardCharsets.UTF_8);
    }
}
