package cn.com.crc.ems.finance.agent.task.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 任务发起契约错误的最小 HTTP 映射，不回显异常原文或请求内容。 */
@RestControllerAdvice(assignableTypes = TaskIntakeController.class)
public final class TaskIntakeExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "CONTRACT_ERROR",
                "message", "request does not satisfy the task intake contract"));
    }
}
