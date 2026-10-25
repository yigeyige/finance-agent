package cn.com.crc.ems.finance.agent.businesspacket.web;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *
 * @Description: 业务决策包接口的可预期业务失败处理，拒绝生成时返回 400 与提示文案。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-24 10:00:00
 */
@RestControllerAdvice(assignableTypes = BusinessDecisionPacketController.class)
public class BusinessDecisionPacketExceptionHandler {

    /** 无草稿等可预期业务失败转为 400，不暴露内部堆栈。 */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException exception) {
        return ResponseEntity.badRequest().body(Map.of("message",
                exception.getMessage() == null ? "业务决策包操作失败" : exception.getMessage()));
    }
}
