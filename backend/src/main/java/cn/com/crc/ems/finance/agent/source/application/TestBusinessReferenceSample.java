package cn.com.crc.ems.finance.agent.source.application;

/**
 *
 * @Description: 承载固定虚构测试人员、岗位、角色、流程节点和权限范围的黄金事实。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
public record TestBusinessReferenceSample(String sampleId, String testEmployeeId, String testPositionCode,
        String testRoleCode, String testProcessNodeCode, String testOrganizationScope,
        TestBusinessReferenceState expectedState) {

    /**
     *
     * @description: 校验参考样本使用完整测试标识且拥有明确的黄金事实状态。
     *
     * @param: sampleId 稳定的测试样本标识。
     * @param: testEmployeeId 虚构测试人员标识。
     * @param: testPositionCode 虚构测试岗位编码。
     * @param: testRoleCode 虚构测试角色编码。
     * @param: testProcessNodeCode 虚构测试流程节点编码。
     * @param: testOrganizationScope 虚构测试组织或权限范围。
     * @param: expectedState 样本的预期黄金事实状态。
     * @throws: IllegalArgumentException 标识不完整、未使用测试前缀或状态缺失时抛出。
     * @author: yigeyige99
     */
    public TestBusinessReferenceSample {
        requireTestValue(sampleId, "TST-REF-", "sample id");
        requireTestValue(testEmployeeId, "TST-EMP-", "employee id");
        requireTestValue(testPositionCode, "TST-POS-", "position code");
        requireTestValue(testRoleCode, "TEST_", "role code");
        requireTestValue(testProcessNodeCode, "TEST_NODE_", "process node code");
        requireTestValue(testOrganizationScope, "ORG-TEST-", "organization scope");
        if (expectedState == null) {
            throw new IllegalArgumentException("test reference state is required");
        }
    }

    /**
     *
     * @description: 拒绝非测试标识，防止参考样本混入真实业务对象。
     *
     * @param: value 待校验标识值。
     * @param: prefix 必须匹配的测试前缀。
     * @param: field 字段用途说明。
     * @throws: IllegalArgumentException 标识为空或未使用要求前缀时抛出。
     * @author: yigeyige99
     */
    private static void requireTestValue(String value, String prefix, String field) {
        if (value == null || value.isBlank() || !value.startsWith(prefix)) {
            throw new IllegalArgumentException("test reference " + field + " is invalid");
        }
    }
}
