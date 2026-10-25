package cn.com.crc.ems.finance.agent.businesspacket.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.com.crc.ems.finance.agent.businesspacket.application.BusinessDecisionPacketService;

/**
 *
 * @Description: 面向 OPERATOR 的业务决策包生成、查看与累积证据入口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-24 10:00:00
 */
@RestController
@RequestMapping("/api/v1/tasks/{taskId}/decision-packet")
public final class BusinessDecisionPacketController {

    private final BusinessDecisionPacketService service;

    public BusinessDecisionPacketController(BusinessDecisionPacketService service) {
        this.service = service;
    }

    /**
     *
     * @description: 生成业务决策包，内容未变化时返回既有最新修订并标识未新增。
     *
     * @param: taskId 当前任务标识
     * @return: 新增或跳过的决策包修订
     * @author: yigeyige99
     */
    @PostMapping
    public BusinessDecisionPacketResponse generate(@PathVariable UUID taskId) {
        return BusinessDecisionPacketResponse.from(service.generate(taskId));
    }

    /** 查看任务最新业务决策包，不存在时返回 404。 */
    @GetMapping("/latest")
    public ResponseEntity<BusinessDecisionPacketResponse> latest(@PathVariable UUID taskId) {
        return service.latest(taskId).map(packet -> ResponseEntity.ok(BusinessDecisionPacketResponse.from(packet)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** 查看任务指定修订的业务决策包，不存在时返回 404。 */
    @GetMapping("/revisions/{revision}")
    public ResponseEntity<BusinessDecisionPacketResponse> revision(@PathVariable UUID taskId,
            @PathVariable int revision) {
        return service.revision(taskId, revision)
                .map(packet -> ResponseEntity.ok(BusinessDecisionPacketResponse.from(packet)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** 查看任务证据工作区按轮累积的全部证据。 */
    @GetMapping("/evidence")
    public List<BusinessEvidenceAccumResponse> evidence(@PathVariable UUID taskId) {
        return service.listAccumulatedEvidence(taskId).stream().map(BusinessEvidenceAccumResponse::from).toList();
    }
}
