package cn.com.crc.ems.finance.agent.source.infrastructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;
import cn.com.crc.ems.finance.agent.source.application.ConfigSourceAdapter;
import cn.com.crc.ems.finance.agent.source.application.ApprovedQuery;
import cn.com.crc.ems.finance.agent.source.application.SourceCatalog;
import cn.com.crc.ems.finance.agent.source.application.SourcePage;
import cn.com.crc.ems.finance.agent.source.application.SourceQuery;
import cn.com.crc.ems.finance.agent.source.application.ScopePage;
import cn.com.crc.ems.finance.agent.source.application.SyntheticScenario;

/** 固定模板、固定 seed、完全离线的合成财务配置源。 */
@Service
public class SyntheticConfigSourceAdapter implements ConfigSourceAdapter {

    private static final String TEMPLATE = "config.employee.references.v1";
    private static final String VERSION = "synthetic-config-v1";
    private static final List<Map<String, Object>> BASE = List.of(record("EMP-SYN-001", "REF-SYN-CURRENT-001", "CURRENT"),
            record("EMP-SYN-001", "REF-SYN-FUTURE-001", "FUTURE"));
    private final SourceCatalog catalog = new SourceCatalog("source-catalog-v1",
            Map.of(TEMPLATE, Map.of("page", "integer", "size", "integer")));

    @Override
    public SourceCatalog catalog() {
        return catalog;
    }

    @Override
    public SourcePage query(SourceQuery query, SyntheticScenario scenario, long seed) {
        if (!TEMPLATE.equals(query.templateCode()) || scenario == null || seed < 0
                || query.parameters().keySet().stream().anyMatch(key -> !catalog.templates().get(TEMPLATE).containsKey(key))) {
            throw new IllegalArgumentException("source query is not registered");
        }
        int page = positive(query.parameters().getOrDefault("page", "1"), "page");
        int size = positive(query.parameters().getOrDefault("size", "2"), "size");
        if (size > 100) {
            throw new IllegalArgumentException("size must not exceed 100");
        }
        if (scenario == SyntheticScenario.PAGINATION_FAILURE && page > 1) {
            throw new IllegalStateException("SYNTHETIC_PAGINATION_FAILURE");
        }
        List<Map<String, Object>> data = scenarioData(scenario, seed);
        int from = Math.min((page - 1) * size, data.size());
        int to = Math.min(from + size, data.size());
        List<Map<String, Object>> records = new ArrayList<>(data.subList(from, to));
        String digest = Sha256Digest.hex(records.toString() + "|" + seed + "|" + scenario);
        return new SourcePage(records, page, size, to < data.size(), VERSION, digest, scenario.name(), (long) data.size());
    }

    @Override
    public ScopePage<Map<String, Object>> query(ApprovedQuery query) {
        if (!catalog.version().equals(query.catalogVersion())) {
            throw new IllegalArgumentException("source catalog version is not approved");
        }
        SourcePage page = query(new SourceQuery(query.templateCode(), query.parameters()), SyntheticScenario.BASELINE, 0);
        return new ScopePage<>(page.records(), page.page(), page.hasNext(),
                page.hasNext() ? "page-" + (page.page() + 1) : null, page.sourceVersion(), page.resultDigest());
    }

    private static List<Map<String, Object>> scenarioData(SyntheticScenario scenario, long seed) {
        List<Map<String, Object>> data = new ArrayList<>(BASE);
        switch (scenario) {
            case CROSS_SOURCE_CONFLICT -> data.add(record("EMP-SYN-001", "REF-SYN-CONFLICT-001", "CONFLICT"));
            case FUTURE_REFERENCE -> data.add(record("EMP-SYN-001", "REF-SYN-FUTURE-002", "FUTURE"));
            case UNKNOWN_GAP -> data.add(record("EMP-SYN-001", "REF-SYN-UNKNOWN-001", "UNKNOWN"));
            case NEW_REFERENCE -> data.add(record("EMP-SYN-001", "REF-SYN-NEW-001", "NEW"));
            default -> { }
        }
        if ((seed & 1) == 1) {
            Collections.reverse(data);
        }
        return data;
    }

    private static Map<String, Object> record(String employee, String reference, String classification) {
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("syntheticEmployeeId", employee);
        value.put("syntheticReferenceId", reference);
        value.put("classification", classification);
        return Map.copyOf(value);
    }

    private static int positive(String value, String name) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < 1) throw new NumberFormatException();
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(name + " must be a positive integer");
        }
    }
}
