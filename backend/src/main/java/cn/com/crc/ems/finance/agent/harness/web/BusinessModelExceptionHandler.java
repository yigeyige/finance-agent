package cn.com.crc.ems.finance.agent.harness.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.com.crc.ems.finance.agent.harness.application.BusinessModelException;

/**
 *
 * @Description: 将模型不可用、证据未就绪和请求错误映射为不泄露提示词的中文响应。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-06 23:00:00
 */
@RestControllerAdvice(assignableTypes = BusinessModelController.class)
public final class BusinessModelExceptionHandler {

    @ExceptionHandler(BusinessModelException.class)
    public ResponseEntity<Map<String, String>> modelUnavailable(BusinessModelException exception) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("code", "MODEL_UNAVAILABLE",
                "message", "模型分析暂时不可用，请稍后重试"));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> stateConflict(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("code", "EVIDENCE_NOT_READY",
                "message", safeStateMessage(exception.getMessage())));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "MODEL_CONTEXT_INVALID",
                "message", "当前任务信息无法用于模型分析，请检查调查范围"));
    }

    private static String safeStateMessage(String message) {
        if (message != null && (message.startsWith("请先") || message.startsWith("调查证据"))) {
            return message;
        }
        return "当前任务状态尚不能进行模型分析";
    }
}
