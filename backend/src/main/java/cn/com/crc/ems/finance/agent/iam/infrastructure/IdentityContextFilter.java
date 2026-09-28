package cn.com.crc.ems.finance.agent.iam.infrastructure;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.application.IdentityResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 在请求边界解析当前身份，并在请求结束时清理上下文。 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public final class IdentityContextFilter extends OncePerRequestFilter {

    private final IdentityResolver resolver;

    public IdentityContextFilter(IdentityResolver resolver) {
        this.resolver = resolver;
    }

    /** 执行身份解析和线程上下文生命周期管理。 */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            var identity = resolver.resolve(request);
            if (identity != null) {
                IdentityContext.set(identity);
            }
            filterChain.doFilter(request, response);
        } finally {
            IdentityContext.clear();
        }
    }
}
