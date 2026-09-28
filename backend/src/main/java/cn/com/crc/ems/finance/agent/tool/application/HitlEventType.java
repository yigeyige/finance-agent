package cn.com.crc.ems.finance.agent.tool.application;

/** 工具请求的人在环生命周期事件。 */
public enum HitlEventType {
    WAITING_APPROVAL,
    APPROVED,
    REJECTED,
    EXPIRED,
    DENIED
}
