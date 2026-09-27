package cn.com.crc.ems.finance.agent.packet.domain;

/** 决策包草稿生命周期状态。 */
public enum PacketDraftStatus {
    CURRENT,
    STALE,
    WITHDRAWN,
    PUBLISHED
}
