package cn.com.crc.ems.finance.agent.evidence.web;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import cn.com.crc.ems.finance.agent.evidence.application.SyntheticEvidenceWorkerResult;
import cn.com.crc.ems.finance.agent.evidence.application.SyntheticEvidenceWorkerService;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/**
 *
 * @Description: 仅在 local/test 环境提供受限的合成证据 Worker 触发入口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-30 21:35:00
 */
@Profile({"local", "test"})
@RestController
@RequestMapping("/api/v1/tasks")
public final class SyntheticEvidenceWorkerController {

    private final SyntheticEvidenceWorkerService workerService;

    public SyntheticEvidenceWorkerController(SyntheticEvidenceWorkerService workerService) {
        this.workerService = workerService;
    }

    /**
     *
     * @description: 执行指定任务的固定合成证据步骤，不接收来源、字段或场景参数。
     *
     * @param: taskId 任务标识
     * @param: idempotencyKey 调用幂等键
     * @return: 仅包含标识、计数和摘要的执行结果
     * @author: yigeyige99
     */
    @PostMapping("/{taskId}/synthetic-evidence-runs")
    public ResponseEntity<SyntheticEvidenceWorkerResponse> run(@PathVariable UUID taskId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        requireIdempotencyKey(idempotencyKey);
        var identity = IdentityContext.require();
        if (!identity.hasRole(RoleCode.OPERATOR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权执行合成证据步骤");
        }
        SyntheticEvidenceWorkerResult result = workerService.run(taskId, identity.userId(), idempotencyKey);
        return ResponseEntity.status(result.replayed() ? HttpStatus.OK : HttpStatus.CREATED)
                .body(SyntheticEvidenceWorkerResponse.from(result));
    }

    private static void requireIdempotencyKey(String value) {
        if (value == null || value.isBlank() || value.length() < 16 || value.length() > 200) {
            throw new IllegalArgumentException("Idempotency-Key must contain 16 to 200 characters");
        }
    }
}
