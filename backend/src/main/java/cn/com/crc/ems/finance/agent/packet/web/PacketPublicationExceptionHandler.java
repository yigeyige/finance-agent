package cn.com.crc.ems.finance.agent.packet.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryStateViolationException;

/**
 *
 * @Description: 将发布契约、状态和对象不存在错误映射为稳定响应。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@RestControllerAdvice(assignableTypes = PacketPublicationController.class)
public final class PacketPublicationExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "CONTRACT_ERROR",
                "message", "决策包发布请求不满足契约"));
    }

    @ExceptionHandler(RepositoryStateViolationException.class)
    public ResponseEntity<Map<String, String>> conflict(RepositoryStateViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("code", "STATE_CONFLICT",
                "message", "发布前重查、草稿或复核状态不允许发布"));
    }

    @ExceptionHandler(RepositoryEntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(RepositoryEntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("code", "NOT_FOUND",
                "message", "发布所需对象不存在"));
    }
}
