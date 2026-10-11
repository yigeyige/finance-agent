package cn.com.crc.ems.finance.agent.task.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;

/**
 *
 * @Description: 将任务诊断查询错误映射为稳定的中文响应。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@RestControllerAdvice(assignableTypes = TaskDiagnosticController.class)
public final class TaskDiagnosticExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "CONTRACT_ERROR",
                "message", "任务诊断请求不满足契约"));
    }

    @ExceptionHandler(RepositoryEntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(RepositoryEntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("code", "NOT_FOUND",
                "message", "任务不存在"));
    }
}
