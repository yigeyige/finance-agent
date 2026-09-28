package cn.com.crc.ems.finance.agent.source;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.source.application.SourceQuery;
import cn.com.crc.ems.finance.agent.source.application.SyntheticScenario;
import cn.com.crc.ems.finance.agent.source.infrastructure.SyntheticConfigSourceAdapter;

class SyntheticConfigSourceAdapterTest {

    @Test
    void sameSeedProducesStableSyntheticPage() {
        SyntheticConfigSourceAdapter adapter = new SyntheticConfigSourceAdapter();
        SourceQuery query = new SourceQuery("config.employee.references.v1", Map.of("page", "1", "size", "2"));
        var first = adapter.query(query, SyntheticScenario.BASELINE, 7);
        var second = adapter.query(query, SyntheticScenario.BASELINE, 7);
        assertThat(first).isEqualTo(second);
        assertThat(first.records()).allMatch(record -> record.get("syntheticEmployeeId").toString().startsWith("EMP-SYN-"));
    }

    @Test
    void rejectsSqlUnknownParametersAndWrites() {
        SyntheticConfigSourceAdapter adapter = new SyntheticConfigSourceAdapter();
        assertThatThrownBy(() -> adapter.query(new SourceQuery("select * from employee", Map.of()),
                SyntheticScenario.BASELINE, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> adapter.query(new SourceQuery("config.employee.references.v1", Map.of("sql", "DROP")),
                SyntheticScenario.BASELINE, 1)).isInstanceOf(IllegalArgumentException.class);
    }
}
