package cn.com.crc.ems.finance.agent.evidence.web;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.evidence.application.ComputationWitnessRepository;
import cn.com.crc.ems.finance.agent.evidence.application.CoverageRecordRepository;
import cn.com.crc.ems.finance.agent.evidence.application.FactSnapshotRepository;
import cn.com.crc.ems.finance.agent.evidence.application.FindingRepository;
import cn.com.crc.ems.finance.agent.evidence.application.ProfessionalConfirmationRepository;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/** 提供当前任务的证据、覆盖度和判断脱敏查询。 */
@RestController
@RequestMapping("/api/v1/tasks/{taskId}/evidence")
public final class EvidenceOverviewController {

    private final FactSnapshotRepository snapshotRepository;
    private final CoverageRecordRepository coverageRepository;
    private final FindingRepository findingRepository;
    private final ComputationWitnessRepository witnessRepository;
    private final ProfessionalConfirmationRepository confirmationRepository;

    public EvidenceOverviewController(FactSnapshotRepository snapshotRepository,
            CoverageRecordRepository coverageRepository, FindingRepository findingRepository,
            ComputationWitnessRepository witnessRepository, ProfessionalConfirmationRepository confirmationRepository) {
        this.snapshotRepository = snapshotRepository;
        this.coverageRepository = coverageRepository;
        this.findingRepository = findingRepository;
        this.witnessRepository = witnessRepository;
        this.confirmationRepository = confirmationRepository;
    }

    /** 查询已批准范围内的证据链；VIEWER 在发布前不允许访问。 */
    @GetMapping
    public EvidenceOverviewResponse current(@PathVariable UUID taskId) {
        var identity = IdentityContext.require();
        if (!identity.hasRole(RoleCode.OPERATOR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权查看发布前证据");
        }
        return EvidenceOverviewResponse.of(taskId, snapshotRepository.findByTaskId(taskId),
                coverageRepository.findByTaskId(taskId), findingRepository.findByTaskId(taskId),
                findingId -> witnessRepository.findByFindingId(findingId).orElse(null),
                findingId -> confirmationRepository.findByFindingId(findingId).orElse(null));
    }
}
