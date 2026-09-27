package cn.com.crc.ems.finance.agent.task.domain;

import java.util.UUID;

/** 由任务、步骤、审批修订和调用序号组成的规范化工具调用键。 */
public record ToolInvocationKey(UUID taskId, UUID stepId, int approvalRevision, int invocationNo) {

    public ToolInvocationKey {
        if (taskId == null || stepId == null) {
            throw new IllegalArgumentException("taskId and stepId are required");
        }
        if (approvalRevision < 1 || invocationNo < 1) {
            throw new IllegalArgumentException("approvalRevision and invocationNo must be positive");
        }
    }

    /** 返回所有入口都必须使用的数据库幂等键格式。 */
    public String value() {
        return taskId + ":" + stepId + ":" + approvalRevision + ":" + invocationNo;
    }
}
