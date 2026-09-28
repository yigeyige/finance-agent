package cn.com.crc.ems.finance.agent.source.application;

/** 合成源可重复切换的验证场景。 */
public enum SyntheticScenario {
    BASELINE,
    COMPLETE_NO_MATCH,
    PARTIAL_NO_MATCH,
    PAGINATION_FAILURE,
    CROSS_SOURCE_CONFLICT,
    FUTURE_REFERENCE,
    UNKNOWN_GAP,
    NEW_REFERENCE
}
