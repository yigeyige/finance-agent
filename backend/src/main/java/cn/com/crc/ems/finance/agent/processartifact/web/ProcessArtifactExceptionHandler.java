package cn.com.crc.ems.finance.agent.processartifact.web;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 文件生成与下载的可预期业务错误。 */
@RestControllerAdvice(assignableTypes = ProcessArtifactController.class)
public final class ProcessArtifactExceptionHandler {
    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handle(RuntimeException exception) {
        return ResponseEntity.badRequest().body(Map.of("message",
                exception.getMessage() == null ? "流程文件操作失败" : exception.getMessage()));
    }
}
