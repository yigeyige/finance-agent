package cn.com.crc.ems.finance.agent.packet.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;

/** 将草稿组装的契约、状态和对象不存在错误映射为稳定响应。 */
@RestControllerAdvice(assignableTypes = PacketDraftController.class)
public final class PacketDraftExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "CONTRACT_ERROR",
                "message", "决策包草稿不满足结构化契约"));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> conflict(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("code", "STATE_CONFLICT",
                "message", "范围已过期或当前草稿不可覆盖"));
    }

    @ExceptionHandler(RepositoryEntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(RepositoryEntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("code", "NOT_FOUND",
                "message", "决策包组装所需对象不存在"));
    }
}
