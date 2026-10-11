package cn.com.crc.ems.finance.agent.task.domain;

/**
 *
 * @Description: 工具调用生命周期状态。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public enum InvocationStatus {
    INTENT_RECORDED, RUNNING, SUCCEEDED, PARTIAL, FAILED, UNKNOWN
}
