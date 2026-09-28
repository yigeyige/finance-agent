package cn.com.crc.ems.finance.agent.source;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.source.application.ApprovedQuery;
import cn.com.crc.ems.finance.agent.source.application.RecheckResult;
import cn.com.crc.ems.finance.agent.source.application.RecheckStatus;
import cn.com.crc.ems.finance.agent.source.application.SourceAdapterErrorCode;
import cn.com.crc.ems.finance.agent.source.application.SourceAdapterUnavailableException;
import cn.com.crc.ems.finance.agent.source.infrastructure.ProductionConfigSourceAdapter;
import cn.com.crc.ems.finance.agent.source.infrastructure.SyntheticConfigSourceAdapter;

class SourceAdapterContractTest {

    @Test
    void syntheticAdapterSatisfiesApprovedQueryContract() {
        var adapter = new SyntheticConfigSourceAdapter();
        var page = adapter.query(new ApprovedQuery("source-catalog-v1", "config.employee.references.v1",
                Map.of("page", "1", "size", "2")));
        assertThat(page.records()).isNotEmpty();
        assertThat(page.sourceWatermark()).isEqualTo("synthetic-config-v1");
        assertThat(page.resultDigest()).hasSize(64);
    }

    @Test
    void productionPlaceholderFailsClosedWithoutSyntheticFallback() {
        var adapter = new ProductionConfigSourceAdapter();
        assertThatThrownBy(adapter::catalog).isInstanceOfSatisfying(SourceAdapterUnavailableException.class,
                error -> assertThat(error.code()).isEqualTo(SourceAdapterErrorCode.SOURCE_CATALOG_REQUIRED));
        assertThatThrownBy(() -> adapter.query(new ApprovedQuery("source-catalog-v1", "template", Map.of())))
                .isInstanceOfSatisfying(SourceAdapterUnavailableException.class,
                        error -> assertThat(error.code()).isEqualTo(SourceAdapterErrorCode.SOURCE_NOT_CONFIGURED));
    }

    @Test
    void recheckContractIsImmutableAndValidated() {
        assertThat(new RecheckResult(RecheckStatus.MATCHED, "NONE", "a".repeat(64)).status())
                .isEqualTo(RecheckStatus.MATCHED);
        assertThatThrownBy(() -> new ApprovedQuery("", "template", Map.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
