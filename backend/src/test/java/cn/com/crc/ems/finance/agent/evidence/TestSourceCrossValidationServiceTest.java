package cn.com.crc.ems.finance.agent.evidence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.evidence.application.SourceConflictCode;
import cn.com.crc.ems.finance.agent.evidence.application.SourceConflictCoordinator;
import cn.com.crc.ems.finance.agent.evidence.application.TestSourceCrossValidationScenario;
import cn.com.crc.ems.finance.agent.evidence.application.TestSourceCrossValidationService;
import cn.com.crc.ems.finance.agent.source.application.SourceAdapterErrorCode;
import cn.com.crc.ems.finance.agent.source.application.SourceAdapterUnavailableException;
import cn.com.crc.ems.finance.agent.source.infrastructure.TestBusinessSourceAdapter;
import cn.com.crc.ems.finance.agent.tool.application.TrustedFieldProjector;

/**
 *
 * @Description: 验证两个固定测试来源的一致、冲突、未知与不可用场景均不会产生单源确认回退。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
class TestSourceCrossValidationServiceTest {

    private final TestSourceCrossValidationService service = new TestSourceCrossValidationService(
            new TestBusinessSourceAdapter(new TrustedFieldProjector()), new SourceConflictCoordinator());

    @Test
    void matchingSourcesResolveOnlyTheRegisteredHighestAuthority() {
        var result = service.validate("TST-EMP-001", TestSourceCrossValidationScenario.MATCHING);

        assertThat(result.conflict().code()).isEqualTo(SourceConflictCode.RESOLVED);
        assertThat(result.conflict().resolvedSourceId()).isEqualTo(TestSourceCrossValidationService.PRIMARY_SOURCE_ID);
        assertThat(result.conflict().candidates()).hasSize(2);
    }

    @Test
    void conflictingAndUnknownSourcesRemainUnknownRegardlessOfAuthorityOrder() {
        var conflict = service.validate("TST-EMP-001", TestSourceCrossValidationScenario.CONFLICT);
        var unknown = service.validate("TST-EMP-001", TestSourceCrossValidationScenario.UNKNOWN);

        assertThat(conflict.conflict().code()).isEqualTo(SourceConflictCode.UNKNOWN_SOURCE_CONFLICT);
        assertThat(conflict.conflict().resolvedSourceId()).isNull();
        assertThat(unknown.conflict().code()).isEqualTo(SourceConflictCode.UNKNOWN_SOURCE_CONFLICT);
        assertThat(unknown.conflict().resolvedSourceId()).isNull();
    }

    @Test
    void unavailableVerificationSourceFailsClosedWithoutPrimaryFallback() {
        assertThatThrownBy(() -> service.validate("TST-EMP-001", TestSourceCrossValidationScenario.VERIFICATION_UNAVAILABLE))
                .isInstanceOfSatisfying(SourceAdapterUnavailableException.class,
                        error -> assertThat(error.code()).isEqualTo(SourceAdapterErrorCode.SOURCE_UNAVAILABLE));
    }
}
