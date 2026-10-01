package cn.com.crc.ems.finance.agent.source.infrastructure;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @Description: 封装首个测试业务只读源的固定虚构样本及其来源版本。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
final class TestBusinessSourceFixture {

    static final String VERSION = "test-business-fixture-v1";
    private static final List<String> FIELDS = List.of("testEmployeeId", "testRoleCode", "testOrganizationScope",
            "effectiveState");

    private final String version;
    private final List<Map<String, String>> records;

    /**
     *
     * @description: 创建不可变且按测试人员标识稳定排序的测试业务样本。
     *
     * @param: version 样本来源版本。
     * @param: records 固定虚构记录。
     * @author: yigeyige99
     */
    TestBusinessSourceFixture(String version, List<Map<String, String>> records) {
        if (version == null || version.isBlank() || records == null || records.isEmpty()) {
            throw new IllegalArgumentException("test business fixture is invalid");
        }
        this.version = version;
        this.records = records.stream().map(TestBusinessSourceFixture::validatedRecord)
                .sorted(Comparator.comparing(record -> record.get("testEmployeeId"))).toList();
    }

    /**
     *
     * @description: 返回首个测试业务源唯一允许使用的固定虚构样本。
     *
     * @return: 已排序且字段完整的测试业务样本。
     * @author: yigeyige99
     */
    static TestBusinessSourceFixture fixed() {
        return new TestBusinessSourceFixture(VERSION, List.of(record("TST-EMP-001", "TEST_OPERATOR", "ORG-TEST-A", "CURRENT"),
                record("TST-EMP-002", "TEST_VIEWER", "ORG-TEST-A", "FUTURE"),
                record("TST-EMP-003", "TEST_REVIEWER", "ORG-TEST-B", "UNKNOWN")));
    }

    String version() {
        return version;
    }

    List<Map<String, String>> records() {
        return records;
    }

    /**
     *
     * @description: 验证样本字段并按目录字段顺序生成不可变记录。
     *
     * @param: record 待校验的测试业务记录。
     * @return: 仅包含登记字段的不可变记录。
     * @throws: IllegalArgumentException 字段集合或取值不符合固定样本契约时抛出。
     * @author: yigeyige99
     */
    private static Map<String, String> validatedRecord(Map<String, String> record) {
        if (record == null || !record.keySet().equals(java.util.Set.copyOf(FIELDS))
                || record.values().stream().anyMatch(value -> value == null || value.isBlank())) {
            throw new IllegalArgumentException("test business fixture record is invalid");
        }
        Map<String, String> ordered = new LinkedHashMap<>();
        FIELDS.forEach(field -> ordered.put(field, record.get(field)));
        return Map.copyOf(ordered);
    }

    /**
     *
     * @description: 创建不含真实身份或生产配置的固定虚构业务记录。
     *
     * @param: employeeId 测试人员标识。
     * @param: roleCode 测试角色编码。
     * @param: organizationScope 测试组织范围。
     * @param: effectiveState 测试生效状态。
     * @return: 固定字段的测试业务记录。
     * @author: yigeyige99
     */
    private static Map<String, String> record(String employeeId, String roleCode, String organizationScope,
            String effectiveState) {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("testEmployeeId", employeeId);
        record.put("testRoleCode", roleCode);
        record.put("testOrganizationScope", organizationScope);
        record.put("effectiveState", effectiveState);
        return record;
    }
}
