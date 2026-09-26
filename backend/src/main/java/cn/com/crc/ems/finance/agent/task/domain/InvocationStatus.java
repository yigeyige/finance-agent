package cn.com.crc.ems.finance.agent.task.domain;

/** 工具调用生命周期状态。 */
public enum InvocationStatus {
    INTENT_RECORDED, RUNNING, SUCCEEDED, PARTIAL, FAILED, UNKNOWN
}
