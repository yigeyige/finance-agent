package cn.com.crc.ems.finance.agent.source.infrastructure;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.shared.domain.JsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;
import cn.com.crc.ems.finance.agent.source.application.ApprovedQuery;
import cn.com.crc.ems.finance.agent.source.application.ConfigSourceAdapter;
import cn.com.crc.ems.finance.agent.source.application.ScopePage;
import cn.com.crc.ems.finance.agent.source.application.SourceAdapterErrorCode;
import cn.com.crc.ems.finance.agent.source.application.SourceAdapterUnavailableException;
import cn.com.crc.ems.finance.agent.source.application.SourceCatalog;
import cn.com.crc.ems.finance.agent.source.application.SourcePage;
import cn.com.crc.ems.finance.agent.source.application.SourceQuery;
import cn.com.crc.ems.finance.agent.source.application.SyntheticScenario;
import cn.com.crc.ems.finance.agent.tool.application.FieldAccessPolicy;
import cn.com.crc.ems.finance.agent.tool.application.TrustedFieldProjector;

/**
 *
 * @Description: 在 local/test 环境以固定虚构样本实现已批准模板的测试业务只读源适配器。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
@Service
@Profile({"local", "test"})
public class TestBusinessSourceAdapter implements ConfigSourceAdapter {

    public static final String CATALOG_VERSION = "test-business-catalog-v1";
    public static final String TEMPLATE = "test.personnel.assignment.v1";
    private static final int MAX_PAGE_SIZE = 100;
    private static final List<String> FIELDS = List.of("testEmployeeId", "testRoleCode", "testOrganizationScope",
            "effectiveState");
    private static final FieldAccessPolicy FIELD_POLICY = new FieldAccessPolicy(Set.copyOf(FIELDS), Set.copyOf(FIELDS),
            Set.of("testRoleCode", "effectiveState"));
    private static final SourceCatalog CATALOG = new SourceCatalog(CATALOG_VERSION,
            Map.of(TEMPLATE, Map.of("page", "integer", "size", "integer")),
            Map.of("test-personnel-assignment", List.of("test-business-primary", "test-business-verification")));

    private final TrustedFieldProjector fieldProjector;
    private final TestBusinessSourceFixture fixture;

    /**
     *
     * @description: 注入可信字段投影器并装载固定测试业务样本。
     *
     * @param: fieldProjector 复用的字段权限投影器。
     * @author: yigeyige99
     */
    @Autowired
    public TestBusinessSourceAdapter(TrustedFieldProjector fieldProjector) {
        this(fieldProjector, TestBusinessSourceFixture.fixed());
    }

    TestBusinessSourceAdapter(TrustedFieldProjector fieldProjector, TestBusinessSourceFixture fixture) {
        if (fieldProjector == null) {
            throw new IllegalArgumentException("field projector is required");
        }
        this.fieldProjector = fieldProjector;
        this.fixture = fixture;
    }

    @Override
    public SourceCatalog catalog() {
        return CATALOG;
    }

    @Override
    public SourcePage query(SourceQuery query, SyntheticScenario scenario, long seed) {
        throw rejected("synthetic scenario query is not supported by the test business source");
    }

    /**
     *
     * @description: 校验已批准查询并从固定测试业务样本返回稳定的受限字段分页结果。
     *
     * @param: query 已批准的目录版本、模板及分页参数。
     * @return: 不含禁止字段的稳定只读来源结果页。
     * @throws: SourceAdapterUnavailableException 目录漂移、样本不可用或查询不在白名单时抛出。
     * @author: yigeyige99
     */
    @Override
    public ScopePage<Map<String, Object>> query(ApprovedQuery query) {
        requireApproved(query);
        TestBusinessSourceFixture availableFixture = requireFixture();
        int page = positive(query.parameters().getOrDefault("page", "1"), "page");
        int size = positive(query.parameters().getOrDefault("size", "20"), "size");
        if (size > MAX_PAGE_SIZE) {
            throw rejected("page size is not approved");
        }
        List<Map<String, Object>> records = availableFixture.records().stream().map(this::project).toList();
        int from = Math.min((page - 1) * size, records.size());
        int to = Math.min(from + size, records.size());
        List<Map<String, Object>> pageRecords = records.subList(from, to);
        boolean hasNext = to < records.size();
        String digestMaterial = pageRecords.stream().map(this::canonicalRecord).reduce("", (left, right) -> left + right);
        return new ScopePage<>(pageRecords, page, hasNext, hasNext ? "page-" + (page + 1) : null,
                availableFixture.version(), Sha256Digest.hex(digestMaterial));
    }

    /**
     *
     * @description: 在读取固定样本前校验目录版本、模板和参数白名单。
     *
     * @param: query 已批准查询。
     * @throws: SourceAdapterUnavailableException 查询未登记或目录版本不一致时抛出。
     * @author: yigeyige99
     */
    private void requireApproved(ApprovedQuery query) {
        if (query == null || !CATALOG_VERSION.equals(query.catalogVersion())) {
            throw new SourceAdapterUnavailableException(SourceAdapterErrorCode.SOURCE_CATALOG_DRIFT,
                    "test business source catalog does not match the approved version");
        }
        if (!TEMPLATE.equals(query.templateCode()) || !CATALOG.templates().get(TEMPLATE).keySet().containsAll(query.parameters().keySet())) {
            throw rejected("test business source query is not registered");
        }
    }

    /**
     *
     * @description: 确认固定测试样本可用且其版本与目录一致。
     *
     * @return: 可用于只读查询的固定测试样本。
     * @throws: SourceAdapterUnavailableException 样本缺失或版本漂移时抛出。
     * @author: yigeyige99
     */
    private TestBusinessSourceFixture requireFixture() {
        if (fixture == null) {
            throw new SourceAdapterUnavailableException(SourceAdapterErrorCode.SOURCE_UNAVAILABLE,
                    "test business source fixture is unavailable");
        }
        if (!TestBusinessSourceFixture.VERSION.equals(fixture.version())) {
            throw new SourceAdapterUnavailableException(SourceAdapterErrorCode.SOURCE_CATALOG_DRIFT,
                    "test business source fixture version does not match the catalog");
        }
        return fixture;
    }

    /**
     *
     * @description: 复用可信字段投影器校验并仅输出目录允许的测试字段。
     *
     * @param: record 固定测试样本记录。
     * @return: 不含未登记字段的只读记录。
     * @author: yigeyige99
     */
    private Map<String, Object> project(Map<String, String> record) {
        var projection = fieldProjector.project(JsonCodec.mapper().valueToTree(record), FIELD_POLICY).readPayload();
        Map<String, Object> projected = new LinkedHashMap<>();
        FIELDS.forEach(field -> projected.put(field, projection.get(field).asText()));
        return Map.copyOf(projected);
    }

    /**
     *
     * @description: 按固定字段顺序拼接记录摘要输入，避免 Map 遍历顺序影响结果摘要。
     *
     * @param: record 已完成字段投影的只读记录。
     * @return: 稳定的摘要输入文本。
     * @author: yigeyige99
     */
    private String canonicalRecord(Map<String, Object> record) {
        return FIELDS.stream().map(field -> field + "=" + record.get(field)).reduce("", (left, right) -> left + "|" + right);
    }

    /**
     *
     * @description: 校验分页参数为正整数并在失败时返回稳定拒绝语义。
     *
     * @param: value 待解析的参数值。
     * @param: name 参数名称。
     * @return: 已校验的正整数。
     * @throws: SourceAdapterUnavailableException 参数非法时抛出。
     * @author: yigeyige99
     */
    private static int positive(String value, String name) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < 1) {
                throw new NumberFormatException();
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw rejected(name + " must be positive");
        }
    }

    /**
     *
     * @description: 构建不会触发外部回退的测试源查询拒绝异常。
     *
     * @param: message 安全的稳定失败说明。
     * @return: 带查询拒绝错误码的异常。
     * @author: yigeyige99
     */
    private static SourceAdapterUnavailableException rejected(String message) {
        return new SourceAdapterUnavailableException(SourceAdapterErrorCode.SOURCE_QUERY_REJECTED, message);
    }
}
