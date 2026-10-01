package cn.com.crc.ems.finance.agent.source.application;

/**
 *
 * @Description: 描述测试业务试点中需要人工复核的非当前确认边界样本。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
public record TestBusinessPilotIssue(String sampleId, TestBusinessReferenceState state, String reason) {

    /**
     *
     * @description: 校验问题项只引用测试样本并保留非当前确认状态。
     *
     * @param: sampleId 稳定的测试样本标识。
     * @param: state 对应黄金事实状态。
     * @param: reason 需要人工复核的固定原因。
     * @throws: IllegalArgumentException 样本、状态或原因缺失时抛出。
     * @author: yigeyige99
     */
    public TestBusinessPilotIssue {
        if (sampleId == null || !sampleId.startsWith("TST-REF-") || state == null || state.isConfirmedCurrent()
                || reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("test business pilot issue is invalid");
        }
    }
}
