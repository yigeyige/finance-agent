package cn.com.crc.ems.finance.agent.emsknowledge.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.iam.web.PermissionGuardInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class EmsBusinessKnowledgePermissionGuardTest {

    @AfterEach
    void clearIdentity() {
        IdentityContext.clear();
    }

    @Test
    void allowsOperatorToReadCatalog() throws Exception {
        HttpServletRequest request = request();
        HttpServletResponse response = mock(HttpServletResponse.class);
        IdentityContext.set(new CurrentIdentity(UUID.randomUUID(), "operator", "操作员", Set.of(RoleCode.OPERATOR)));

        assertThat(new PermissionGuardInterceptor().preHandle(request, response, null)).isTrue();
    }

    @Test
    void rejectsViewerBeforeCatalogContentIsReturned() throws Exception {
        HttpServletRequest request = request();
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        IdentityContext.set(new CurrentIdentity(UUID.randomUUID(), "viewer", "查看员", Set.of(RoleCode.VIEWER)));

        assertThat(new PermissionGuardInterceptor().preHandle(request, response, null)).isFalse();
        verify(response).setStatus(HttpStatus.FORBIDDEN.value());
    }

    private static HttpServletRequest request() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/ems-business-knowledge");
        return request;
    }
}
