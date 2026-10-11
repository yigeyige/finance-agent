package cn.com.crc.ems.finance.agent.packet.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryStateViolationException;
import cn.com.crc.ems.finance.agent.task.domain.InvalidTaskTransitionException;

/**
 *
 * @Description: 将发布生命周期契约、状态和对象不存在错误映射为稳定响应。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@RestControllerAdvice(assignableTypes = PacketLifecycleController.class)
public final class PacketLifecycleExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "CONTRACT_ERROR",
                "message", "发布查看、交接或关闭请求不满足契约"));
    }

    @ExceptionHandler({RepositoryStateViolationException.class, InvalidTaskTransitionException.class})
    public ResponseEntity<Map<String, String>> conflict(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("code", "STATE_CONFLICT",
                "message", "发布生命周期当前状态不允许此操作"));
    }

    @ExceptionHandler(RepositoryEntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(RepositoryEntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("code", "NOT_FOUND",
                "message", "已发布决策包或任务不存在"));
    }
}
