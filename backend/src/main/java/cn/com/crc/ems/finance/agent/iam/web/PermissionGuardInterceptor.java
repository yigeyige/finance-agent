package cn.com.crc.ems.finance.agent.iam.web;

import java.util.Set;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @Description: 按稳定角色矩阵执行服务端权限守卫，前端导航不参与授权决策。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Component
public final class PermissionGuardInterceptor implements HandlerInterceptor, WebMvcConfigurer {

    /** 注册 API 权限拦截器。 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns("/api/**");
    }

    /** 拒绝没有可信身份或不满足角色矩阵的 API 请求。 */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/identity")) {
            return true;
        }
        if (!path.startsWith("/api/v1/tasks") && !path.startsWith("/api/v1/tool-catalog")
                && !path.startsWith("/api/v1/business-scene-skills")
                && !path.startsWith("/api/v1/ems-business-knowledge")
                && !path.startsWith("/api/v1/model-configurations")
                && !path.startsWith("/api/v1/ems-database-configurations")
                && !path.startsWith("/api/v1/data-exploration")
                && !path.startsWith("/api/tasks/")) {
            return true;
        }
        var identity = IdentityContext.current();
        if (identity == null) {
            return reject(response, HttpStatus.UNAUTHORIZED, "当前请求缺少有效身份");
        }
        if (path.startsWith("/api/v1/tasks") && !allowedTaskRequest(request, identity.roles())) {
            return reject(response, HttpStatus.FORBIDDEN, "当前身份无权执行此任务操作");
        }
        if (path.startsWith("/api/v1/tool-catalog") && !identity.hasRole(RoleCode.PLATFORM_ADMIN)) {
            return reject(response, HttpStatus.FORBIDDEN, "当前身份无权管理平台配置");
        }
        if (path.startsWith("/api/v1/business-scene-skills") && !identity.hasRole(RoleCode.OPERATOR)) {
            return reject(response, HttpStatus.FORBIDDEN, "当前身份无权维护业务场景 Skill");
        }
        if (path.startsWith("/api/v1/ems-business-knowledge") && !identity.hasRole(RoleCode.OPERATOR)) {
            return reject(response, HttpStatus.FORBIDDEN, "当前身份无权查看 EMS 业务知识域");
        }
        if (path.startsWith("/api/v1/model-configurations") && !identity.hasRole(RoleCode.PLATFORM_ADMIN)) {
            return reject(response, HttpStatus.FORBIDDEN, "当前身份无权维护大模型配置");
        }
        if (path.startsWith("/api/v1/ems-database-configurations/enabled")) {
            return true;
        }
        if (path.startsWith("/api/v1/ems-database-configurations") && !identity.hasRole(RoleCode.PLATFORM_ADMIN)) {
            return reject(response, HttpStatus.FORBIDDEN, "当前身份无权维护 EMS 数据库配置");
        }
        if (path.startsWith("/api/v1/data-exploration") && !identity.hasRole(RoleCode.PLATFORM_ADMIN)) {
            return reject(response, HttpStatus.FORBIDDEN, "当前身份无权使用数据探索");
        }
        if (path.startsWith("/api/tasks/") && !identity.hasRole(RoleCode.OPERATOR)
                && !identity.hasRole(RoleCode.VIEWER)) {
            return reject(response, HttpStatus.FORBIDDEN, "当前身份无权查看任务事件");
        }
        return true;
    }

    private static boolean allowedTaskRequest(HttpServletRequest request, Set<RoleCode> roles) {
        if (roles.contains(RoleCode.PLATFORM_ADMIN)) {
            return true;
        }
        if (HttpMethod.GET.matches(request.getMethod())) {
            return roles.contains(RoleCode.OPERATOR) || roles.contains(RoleCode.VIEWER);
        }
        return roles.contains(RoleCode.OPERATOR);
    }

    private static boolean reject(HttpServletResponse response, HttpStatus status, String message) throws Exception {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":\"" + status.value() + "\",\"message\":\"" + message + "\"}");
        return false;
    }
}
