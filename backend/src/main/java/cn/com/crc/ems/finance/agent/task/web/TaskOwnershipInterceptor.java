package cn.com.crc.ems.finance.agent.task.web;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.task.application.InvestigationTaskRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @Description: 在任务子资源请求边界按可信发起人校验归属，覆盖只读和写入接口。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-05 00:00:00
 */
@Component
public final class TaskOwnershipInterceptor implements HandlerInterceptor, WebMvcConfigurer {
    private final InvestigationTaskRepository tasks;

    public TaskOwnershipInterceptor(InvestigationTaskRepository tasks) { this.tasks = tasks; }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns("/api/v1/tasks/**", "/api/tasks/**");
    }

    /** 仅在路径带任务 UUID 时校验，列表及新建请求交由原有入口处理。 */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        String prefix = path.startsWith("/api/v1/tasks/") ? "/api/v1/tasks/"
                : path.startsWith("/api/tasks/") ? "/api/tasks/" : null;
        if (prefix == null) return true;
        String part = path.substring(prefix.length()).split("/", 2)[0];
        UUID taskId;
        try { taskId = UUID.fromString(part); }
        catch (IllegalArgumentException invalid) { return true; }
        var actor = IdentityContext.current();
        if (actor == null) return reject(response, HttpStatus.UNAUTHORIZED);
        if (actor.hasRole(RoleCode.PLATFORM_ADMIN)) return true;
        if (tasks.findById(taskId).filter(task -> task.initiatorUserId().equals(actor.userId())).isPresent()) return true;
        return reject(response, HttpStatus.NOT_FOUND);
    }

    private static boolean reject(HttpServletResponse response, HttpStatus status) throws Exception {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":\"" + status.value() + "\",\"message\":\"任务不存在或无权访问\"}");
        return false;
    }
}
