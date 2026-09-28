package cn.com.crc.ems.finance.agent.source.infrastructure;

import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.source.application.ApprovedQuery;
import cn.com.crc.ems.finance.agent.source.application.ConfigSourceAdapter;
import cn.com.crc.ems.finance.agent.source.application.ScopePage;
import cn.com.crc.ems.finance.agent.source.application.SourceAdapterErrorCode;
import cn.com.crc.ems.finance.agent.source.application.SourceAdapterUnavailableException;
import cn.com.crc.ems.finance.agent.source.application.SourceCatalog;
import cn.com.crc.ems.finance.agent.source.application.SourcePage;
import cn.com.crc.ems.finance.agent.source.application.SourceQuery;
import cn.com.crc.ems.finance.agent.source.application.SyntheticScenario;

/** prod profile 下真实只读源的阻断占位，不返回合成或伪造成功数据。 */
@Service
@Profile("prod")
public class ProductionConfigSourceAdapter implements ConfigSourceAdapter {

    @Override
    public SourceCatalog catalog() {
        throw new SourceAdapterUnavailableException(SourceAdapterErrorCode.SOURCE_CATALOG_REQUIRED,
                "production source catalog is not configured");
    }

    @Override
    public SourcePage query(SourceQuery query, SyntheticScenario scenario, long seed) {
        throw unavailable();
    }

    @Override
    public ScopePage<Map<String, Object>> query(ApprovedQuery query) {
        throw unavailable();
    }

    private static SourceAdapterUnavailableException unavailable() {
        return new SourceAdapterUnavailableException(SourceAdapterErrorCode.SOURCE_NOT_CONFIGURED,
                "production read-only source is not configured");
    }
}
